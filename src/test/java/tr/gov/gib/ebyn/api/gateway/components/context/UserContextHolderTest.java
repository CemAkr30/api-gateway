package tr.gov.gib.ebyn.api.gateway.components.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tr.gov.gib.ebyn.api.gateway.responses.SecurityTokenCheckResponse.UserInfo;

import static org.junit.jupiter.api.Assertions.*;

class UserContextHolderTest {

  @AfterEach
  void tearDown() {
    // Clean up after each test
    UserContextHolder.clear();
  }

  @Test
  void testGetContext_ReturnsNewContextWhenEmpty() {
    UserContextHolder.UserContext context = UserContextHolder.getContext();
    assertNotNull(context);
    assertNull(context.getUserInfo());
  }

  @Test
  void testSetAndGetContext() {
    // Create test data
    UserInfo userInfo = new UserInfo();
    userInfo.setKullaniciKodu("test-user");
    userInfo.setAdSoyad("test@example.com");

    // Set context
    UserContextHolder.UserContext newContext = new UserContextHolder.UserContext();
    newContext.setUserInfo(userInfo);
    UserContextHolder.setContext(newContext);

    // Verify
    UserContextHolder.UserContext retrievedContext = UserContextHolder.getContext();
    assertNotNull(retrievedContext);
    assertEquals(userInfo, retrievedContext.getUserInfo());
    assertEquals("test-user", retrievedContext.getUserInfo().getKullaniciKodu());
    assertEquals("test@example.com", retrievedContext.getUserInfo().getAdSoyad());
  }

  @Test
  void testClearContext() {
    // Set some context first
    UserContextHolder.UserContext context = new UserContextHolder.UserContext();
    UserInfo userInfo = new UserInfo();
    context.setUserInfo(userInfo);
    UserContextHolder.setContext(context);

    // Clear and verify
    UserContextHolder.clear();
    UserContextHolder.UserContext clearedContext = UserContextHolder.getContext();
    assertNotNull(clearedContext);
    assertNull(clearedContext.getUserInfo());
  }

  @Test
  void testThreadLocalIsolation() throws InterruptedException {
    // Set context in main thread
    UserInfo mainThreadUserInfo = new UserInfo();
    mainThreadUserInfo.setKullaniciKodu("main-thread");
    UserContextHolder.UserContext mainContext = new UserContextHolder.UserContext();
    mainContext.setUserInfo(mainThreadUserInfo);
    UserContextHolder.setContext(mainContext);

    // Verify in main thread
    assertEquals("main-thread", UserContextHolder.getContext().getUserInfo().getKullaniciKodu());

    // Create another thread
    Thread otherThread = new Thread(() -> {
      // Should have empty context in new thread
      assertNull(UserContextHolder.getContext().getUserInfo());

      // Set different context in other thread
      UserInfo otherThreadUserInfo = new UserInfo();
      otherThreadUserInfo.setKullaniciKodu("other-thread");
      UserContextHolder.UserContext otherContext = new UserContextHolder.UserContext();
      otherContext.setUserInfo(otherThreadUserInfo);
      UserContextHolder.setContext(otherContext);

      // Verify in other thread
      assertEquals("other-thread", UserContextHolder.getContext().getUserInfo().getKullaniciKodu());
    });

    otherThread.start();
    otherThread.join();

    // Verify main thread context remains unchanged
    assertEquals("main-thread", UserContextHolder.getContext().getUserInfo().getKullaniciKodu());
  }

  @Test
  void testUserContextGetterSetter() {
    UserContextHolder.UserContext context = new UserContextHolder.UserContext();
    assertNull(context.getUserInfo());

    UserInfo userInfo = new UserInfo();
    userInfo.setKullaniciKodu("test-id");
    context.setUserInfo(userInfo);

    assertEquals(userInfo, context.getUserInfo());
    assertEquals("test-id", context.getUserInfo().getKullaniciKodu());
  }
}