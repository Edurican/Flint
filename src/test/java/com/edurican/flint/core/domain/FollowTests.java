package com.edurican.flint.core.domain;

import com.edurican.flint.core.enums.UserRoleEnum;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.storage.FollowRepository;
import com.edurican.flint.storage.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.edurican.flint.core.support.error.ErrorType.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FollowTests {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    private static int MAX_USER = 10000;
    private static int MAX_FOLLOW_LOOP = 100000;
    private static int MAX_THREADS = 10;
    private static List<Long> userIds = new ArrayList<>();

    /**
     * 임시 User 추가
     */
    @BeforeAll
    static void setUpAll(@Autowired FollowRepository followRepository, @Autowired UserRepository userRepository) {

        followRepository.deleteAll();
        userRepository.deleteAll();

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= MAX_USER; i++) {
            String username = "user" + i;
            String password = "password" + i;
            String email = "email" + i + "@email.com";
            users.add(new User(username, password, email, UserRoleEnum.USER));
        }

        List<User> savedUsers = userRepository.saveAll(users);
        userIds = savedUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @BeforeEach
    public void setUp() {
        followRepository.deleteAll();
    }

    /**
     * 테스트 종료시 모두 삭제
     */
    @AfterAll
    static void tearDownAll(@Autowired FollowRepository followRepository, @Autowired UserRepository userRepository) {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * 팔로우 싱글 테스트
     */
    @Test
    @DisplayName("팔로우 싱글 테스트")
    public void followTest() {
        Long userId = userIds.get(0);
        Long targetId = userIds.get(1);

        followService.follow(userId, targetId);

        assertThat(followRepository.existsByFollowerIdAndFollowingId(userId, targetId))
                .isTrue();
    }

    /**
     * 언팔로우 싱글 테스트
     */
    @Test
    @DisplayName("언팔로우 싱글 테스트")
    public void unfollowTest() {
        Long userId = userIds.get(0);
        Long targetId = userIds.get(1);

        followService.follow(userId, targetId);

        followService.unfollow(userId, targetId);

        assertThat(followRepository.existsByFollowerIdAndFollowingId(userId, targetId))
                .isFalse();
    }

    /**
     * 팔로우 멀티 테스트
     * 멀티쓰레딩 처리하여 무작위로 팔로우
     * - Map으로 follower, following 카운트 저장
     * - UserEntity의 follower, following 카운트를 얻음
     * - 서로 동일한지 비교
     */
    @Test
    @DisplayName("팔로우 멀티 테스트")
    public void followMultiTest() throws InterruptedException {

        Map<Long, Integer> followerCounts = new ConcurrentHashMap<>();
        Map<Long, Integer> followingCounts = new ConcurrentHashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
        CountDownLatch latch = new CountDownLatch(MAX_FOLLOW_LOOP);

        for (int i = 0; i < MAX_FOLLOW_LOOP; i++) {
            executorService.submit(() -> {
                try {
                    Long followerId = userIds.get((int) (Math.random() * userIds.size()));
                    Long followingId = userIds.get((int) (Math.random() * userIds.size()));

                    followService.follow(followerId, followingId);

                    followerCounts.merge(followingId, 1, Integer::sum);
                    followingCounts.merge(followerId, 1, Integer::sum);
                } catch (CoreException e) {
                    if (!(e.getErrorType() == CANNOT_FOLLOW_SELF) &&
                            !(e.getErrorType() == ALREADY_FOLLOWING)) {
                        System.out.println("FOLLOW: " + e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executorService.shutdown();

        // 유저의 팔로잉 개수 비교
        followingCounts.forEach((userId, expectedCount) -> {
            int actualCount = followRepository.countByFollowerId(userId);
            assertThat(actualCount).isEqualTo(expectedCount);
        });

        // 유저의 팔로워 개수 비교
        followerCounts.forEach((userId, expectedCount) -> {
            int actualCount = followRepository.countByFollowingId(userId);
            assertThat(actualCount).isEqualTo(expectedCount);
        });
    }

    /**
     * 언팔로우 멀티 테스트
     * 멀티쓰레딩 처리하여 모두 언팔로우하기
     * - 각각의 쓰레드가 유저 한명을 기준삼아 진행
     * - FollowEntity에 FollowerId == userId라면 unfollow 실행
     * - UserEntity의 follower, following count가 0이어야함
     * - FollowEntity에는 아무것도 없어야함
     */
    @Test
    @DisplayName("언팔로우 싱글 테스트")
    public void unfollowMultiTest() throws InterruptedException {

        followMultiTest();

        System.out.println("데이터 생성 완료");

        ConcurrentLinkedQueue<Follow> follows = new ConcurrentLinkedQueue<>(followRepository.findAll());

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
        CountDownLatch latch = new CountDownLatch(follows.size());

        for (Follow follow : follows) {
            executorService.submit(() -> {
                try {
                    followService.unfollow(follow.getFollowerId(), follow.getFollowingId());
                } catch (CoreException e) {
                    if (!(e.getErrorType() == CANNOT_FOLLOW_SELF) &&
                            !(e.getErrorType() == NOT_FOLLOWING)) {
                        System.out.println("UNFOLLOW: " + e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(120, TimeUnit.SECONDS);
        executorService.shutdown();

        for (Long userId : userIds) {
            userRepository.findById(userId).ifPresent(user -> {
                int followerCount = user.getFollowersCount();
                assertThat(followerCount).isEqualTo(0);

                int followingCount = user.getFollowingCount();
                assertThat(followingCount).isEqualTo(0);
            });
        }

        Long totalFollowCount = followRepository.count();
        assertThat(totalFollowCount.longValue()).isEqualTo(0);
    }

}