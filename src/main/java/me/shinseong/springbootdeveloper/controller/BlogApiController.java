package me.shinseong.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.shinseong.springbootdeveloper.domain.Article;
import me.shinseong.springbootdeveloper.dto.AddArticleRequest;
import me.shinseong.springbootdeveloper.dto.ArticleResponse;
import me.shinseong.springbootdeveloper.dto.UpdateArticleRequest;
import me.shinseong.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController // HTTP Response Body에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

    private final BlogService blogService;


    // http://localhost:8080/api/articles
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(articles);
    }

    // http://localhost:8080/api/articles/{id}
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable Long id) {
        Article article = blogService.findById(id);

        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest
                                                      request, Principal principal) {
        Article savedArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }


    // http://localhost:8080/sample/
    @GetMapping("sample")
    public String hello() {
        return "hello";
    }

    // http://localhost:8080/sample/variable1/{String value}
    @GetMapping(value = "/sample/variable1/{val}")
    public String getVar1(@PathVariable String val) {
        return val;
    }

    // http://localhost:8080/sample/variable2/{String value}
    @GetMapping(value = "/sample/variable2/{val}")
    public String getVar2(@PathVariable("val") String value) {
        return value;
    }

    // http://localhost:8080/sample/requestInfo?{parameters}
    @GetMapping(value = "/sample/requestInfo")
    public String getRequestParam(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String organization) {
        return name + " " + email + " " + organization;
    }

    // http://localhost:8080/sample/request?key1=val1&key2=val2...
    @GetMapping("/sample/request2")
    public String getRequestParam2(@RequestParam Map<String, String> param) {
        StringBuilder sb = new StringBuilder();

        param.entrySet().forEach(map -> {
            sb.append(map.getKey() + " : " + map.getValue() + "\n");
        });
        return sb.toString();
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){//void는 응답본문바디가 사용한다.
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id,
                                                 @RequestBody UpdateArticleRequest request){
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok().body(updatedArticle);
    }


}