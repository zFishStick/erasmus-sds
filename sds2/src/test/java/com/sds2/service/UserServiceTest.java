package com.sds2.service;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sds2.classes.entity.Route;
import com.sds2.classes.entity.User;
import com.sds2.classes.entity.Waypoint;
import com.sds2.classes.enums.RouteTravelMode;
import com.sds2.classes.request.UserRequest;
import com.sds2.dto.RouteDTO;
import com.sds2.dto.UserDTO;
import com.sds2.repository.RoutesRepository;
import com.sds2.repository.UserRepository;

class UserServiceTest {

    private UserService userService;
    private Map<Long, User> userStore;
    private Map<String, User> emailIndex;
    private Map<Long, List<Route>> routesByUser;

    @BeforeEach
    void setup() {
        userStore = new HashMap<>();
        emailIndex = new HashMap<>();
        routesByUser = new HashMap<>();

        UserRepository userRepoProxy = (UserRepository) Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class[] { UserRepository.class },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String name = method.getName();
                        if ("findByEmail".equals(name) && args != null && args.length == 1) {
                            return emailIndex.get(args[0]);
                        } else if ("findById".equals(name) && args != null && args.length == 1) {
                            Long id = (Long) args[0];
                            return Optional.ofNullable(userStore.get(id));
                        } else if ("save".equals(name) && args != null && args.length == 1) {
                            User u = (User) args[0];
                            if (u.getId() == null) {
                                long newId = userStore.keySet().stream().mapToLong(Long::longValue).max().orElse(0L) + 1L;
                                u.setId(newId);
                            }
                            userStore.put(u.getId(), u);
                            emailIndex.put(u.getEmail(), u);
                            return u;
                        }
                        // Fallback: return reasonable default for common JpaRepository methods
                        if ("existsById".equals(name) && args != null && args.length == 1) {
                            return userStore.containsKey((Long) args[0]);
                        }
                        return null;
                    }
                });

        RoutesRepository routesRepoProxy = (RoutesRepository) Proxy.newProxyInstance(
                RoutesRepository.class.getClassLoader(),
                new Class[] { RoutesRepository.class },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String name = method.getName();
                        if ("findAllByUserId".equals(name) && args != null && args.length == 1) {
                            Long userId = (Long) args[0];
                            return routesByUser.getOrDefault(userId, Collections.emptyList());
                        }
                        return Collections.emptyList();
                    }
                });

        userService = new UserService(userRepoProxy, routesRepoProxy);
    }

    @Test
    void registerUser_success_whenEmailNotExists() {
        UserRequest req = new UserRequest();
        req.setEmail("a@x.com");
        req.setUsername("alice");
        req.setPassword("pass");

        UserDTO dto = userService.registerUser(req);
        assertNotNull(dto);
        assertEquals("alice", dto.username());
        assertEquals("a@x.com", dto.email());
        // ensure stored
        User stored = userStore.get(dto.id());
        assertNotNull(stored);
        assertNotEquals("pass", stored.getPassword()); // hashed
    }

    @Test
    void registerUser_returnsNull_whenEmailExists() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("bob@x.com");
        existing.setUsername("bob");
        existing.setPassword("hashed");
        userStore.put(1L, existing);
        emailIndex.put("bob@x.com", existing);

        UserRequest req = new UserRequest();
        req.setEmail("bob@x.com");
        req.setUsername("bobby");
        req.setPassword("newpass");

        UserDTO dto = userService.registerUser(req);
        assertNull(dto);
    }

    @Test
    void getUserById_returnsDto_whenFound() {
        User u = new User();
        u.setId(10L);
        u.setUsername("charlie");
        u.setEmail("c@x.com");
        userStore.put(10L, u);
        emailIndex.put("c@x.com", u);

        UserDTO dto = userService.getUserById(10L);
        assertNotNull(dto);
        assertEquals(10L, dto.id());
        assertEquals("charlie", dto.username());
    }

    @Test
    void getUserById_returnsNull_whenNotFound() {
        UserDTO dto = userService.getUserById(999L);
        assertNull(dto);
    }

    @Test
    void getUserByEmail_returnsDto_whenFound() {
        User u = new User();
        u.setId(20L);
        u.setUsername("d");
        u.setEmail("d@x.com");
        userStore.put(20L, u);
        emailIndex.put("d@x.com", u);

        UserDTO dto = userService.getUserByEmail("d@x.com");
        assertNotNull(dto);
        assertEquals("d@x.com", dto.email());
    }

    @Test
    void getUserByEmail_returnsNull_whenNotFound() {
        assertNull(userService.getUserByEmail("no@x.com"));
    }

    @Test
    void getUserRoutes_returnsMappedDtos_forUserWithRoutes() {
        // prepare user
        User u = new User();
        u.setId(30L);
        u.setUsername("ruser");
        u.setEmail("r@x.com");
        userStore.put(30L, u);
        emailIndex.put("r@x.com", u);

        // prepare route
        Waypoint origin = new Waypoint();
        origin.setId(1L);
        origin.setName("Start");
        Waypoint dest = new Waypoint();
        dest.setId(2L);
        dest.setName("End");
        Waypoint mid = new Waypoint();
        mid.setId(3L);
        mid.setName("Mid");

        Route route = new Route();
        route.setRouteIdentifier("route-1");
        route.setCity("CityX");
        route.setCountry("CountryY");
        route.setOrigin(origin);
        route.setDestination(dest);
        route.setIntermediates(Arrays.asList(mid));
        route.setTravelMode(RouteTravelMode.DRIVING);

        routesByUser.put(30L, Arrays.asList(route));

        List<RouteDTO> dtos = userService.getUserRoutes(30L);
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        RouteDTO rdto = dtos.get(0);
        assertEquals("route-1", rdto.routeIdentifier());
        assertEquals("Start", rdto.origin());
        assertEquals("End", rdto.destination());
        assertEquals(Collections.singletonList("Mid"), rdto.intermediates());
    }

    @Test
    void getUserRoutes_returnsEmptyList_whenNoRoutes() {
        List<RouteDTO> dtos = userService.getUserRoutes(555L);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void removeWaypointFromUser_success_and_failure_cases() {
        User user = new User();
        user.setId(40L);
        user.setUsername("wpuser");
        user.setEmail("wp@x.com");
        Waypoint wp1 = new Waypoint();
        wp1.setId(100L);
        wp1.setName("A");
        Waypoint wp2 = new Waypoint();
        wp2.setId(101L);
        wp2.setName("B");
        user.setSavedWaypoints(new HashSet<>(Arrays.asList(wp1, wp2)));
        userStore.put(40L, user);
        emailIndex.put("wp@x.com", user);

        String res1 = userService.removeWaypointFromUser(100L, 40L);
        assertEquals("Waypoint removed from user successfully", res1);
        assertEquals(1, userStore.get(40L).getSavedWaypoints().size());

        String res2 = userService.removeWaypointFromUser(999L, 40L);
        assertEquals("Waypoint not found for user", res2);

        String res3 = userService.removeWaypointFromUser(101L, 9999L);
        assertEquals("User not found", res3);
    }
}