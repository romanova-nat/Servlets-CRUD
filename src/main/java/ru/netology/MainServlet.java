package ru.netology;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private final String GET = "GET";
  private final String POST = "POST";
  private final String DELETE = "DELETE";

  private PostController controller;

  @Override
  public void init() {
    // отдаём список пакетов, в которых нужно искать аннотированные классы
    final var context = new AnnotationConfigApplicationContext("ru.netology");
    controller = (PostController) context.getBean("postController");
    final var repository = context.getBean(PostRepository.class);
    final var service = context.getBean(PostService.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого

    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();

      // primitive routing
      if (method.equals(GET) && path.equals("/api/posts")) {
        controller.all(resp);
        return;
      }
      if (method.equals(GET) && path.matches("/api/posts/\\d+")) {
        // easy way
        final var id = parsMethod(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals(POST) && path.equals("/api/posts")) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(DELETE) && path.matches("/api/posts/\\d+")) {
        // easy way
        final var id = parsMethod(path);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  public long parsMethod (String path) {
    return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
  }
}