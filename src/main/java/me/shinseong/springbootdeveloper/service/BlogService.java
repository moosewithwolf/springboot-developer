package me.shinseong.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.shinseong.springbootdeveloper.domain.Article;
import me.shinseong.springbootdeveloper.dto.AddArticleRequest;
import me.shinseong.springbootdeveloper.dto.UpdateArticleRequest;
import me.shinseong.springbootdeveloper.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor // Lombok 어노테이션. 클래스 내 final 또는 @NotNull 필드에 대해 자동으로 생성자를 생성함.
// 여기서는 blogRepository가 final이므로, 이 필드를 매개변수로 받는 생성자가 자동으로 만들어짐.
// 생성자를 직접 작성하지 않아도 Spring이 DI(Dependency Injection)로 Repository Bean을 주입할 수 있음.

@Service // Spring이 이 클래스를 Bean으로 관리하도록 지정.
// Service 계층으로 등록되며, Controller에서 @Autowired 또는 생성자 주입으로 사용할 수 있음.

public class BlogService {
    private final BlogRepository blogRepository;
    // BlogRepository 타입의 필드 선언, final로 지정하여 한 번 주입된 후 변경 불가.

    // Service 계층에서는 DB 접근 로직을 직접 구현하지 않고 Repository가 제공하는 메서드(save, findAll 등)를 사용함.
    //    public BlogService(BlogRepository blogRepository) {
    //        this.blogRepository = blogRepository;
    //    }

    // 블로그 글 추가 메서드
    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        // DB에 저장된 모든 Article Entity를 조회하는 메서드
        return blogRepository.findAll();
        // Repository의 findAll() 메서드를 호출하면 DB의 모든 Article 객체를 List<Article> 형태로 반환함.
        // Service 계층은 조회 결과를 그대로 Controller나 다른 계층에 전달함.
    }

    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(Long id){
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(Long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        article.update(request.getTitle(), request.getContent());
        return article;
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " +
                        id));
        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }
    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());
        return article;
    }
    // 게시글을 작성한 유저인지 확인
    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().
                getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
