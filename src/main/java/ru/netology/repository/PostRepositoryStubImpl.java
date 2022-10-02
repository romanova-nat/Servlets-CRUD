package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

  @Repository
  public class PostRepositoryStubImpl implements PostRepository {

    private ConcurrentHashMap<Long, Post> allPosts = new ConcurrentHashMap(); // для хранения 'ключ-значение' всех постов
    AtomicLong counter = new AtomicLong(1);

    public List<Post> all() {  // возвращает всю коллекцию
      return new LinkedList<>(allPosts.values());
    }

    public Optional<Post> getById(long id) {
      if (allPosts.containsKey(id)) {
        return Optional.of(allPosts.get(id));
      } else {
        throw new NotFoundException("Нет такого ID");
      }
    }

    public Post save(Post post) {
      if (post.getId() == 0) {  // если не указали id, то счетчиком прибавляем и записываем в ConcurrentHashMap
        counter.incrementAndGet();
        post.setId(counter.get());
        allPosts.put(post.getId(), post);
        return post;
//            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }

      if (allPosts.containsKey(post.getId())) { // если такой id уже есть, то меняем content
        Post changingContent = allPosts.get(post.getId());
        changingContent.setContent(post.getContent());
        return changingContent;
      }

      if (post.getId() < 0) {
        throw new NotFoundException("Нельзя добавить элемент с отрицательным значением");
      }

      allPosts.put(post.getId(), post);
      return post;
    }

    public void removeById(long id) {
      if (allPosts.containsKey(id)) {
        allPosts.remove(id);
      } else {
        throw new NotFoundException("Нет такого ID");
      }
    }
  }
