package com.hatchways.backEnd.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hatchways.backEnd.dao.PostClient;
import com.hatchways.backEnd.exceptions.DirectionSortByError;
import com.hatchways.backEnd.exceptions.TagNotFound;
import com.hatchways.backEnd.model.Post;
import com.hatchways.backEnd.model.PostBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;



@Api(description = "simple backend JSON API to fecth posts")
@RestController
@RequestMapping (value = "/api")
public class Controller {

    private final PostClient postClient;

    public Controller(PostClient postClient) {
        this.postClient = postClient;
    }

    @ApiOperation(value = "Return a success true on call")
    @GetMapping("/ping")
    public ResponseEntity ping(HttpServletResponse response){
        Boolean status =false;
        int statusCode = response.getStatus();
        if (statusCode == 200){
            return  ResponseEntity.ok().body("success :"+!status);
    }
    else{
        return new ResponseEntity(new Error(), HttpStatus.BAD_REQUEST);
    }
    }

    @ApiOperation(value = "Return list of posts with query parameters (tags, sortBy,direction) with tags required")
    @GetMapping("/posts")
    public PostBean getallPosts(@RequestParam("tags") List<String> tag, HttpServletRequest req)  {
        List<CompletableFuture<PostBean>> allFutures = new ArrayList<>();
        PostBean postBean =new PostBean();
        List<Post> posts = new ArrayList<>();
        ArrayList<String> arraysortBy = new ArrayList<String>();
        arraysortBy.add("id");
        arraysortBy.add("reads");
        arraysortBy.add("likes");
        arraysortBy.add("popularity");

        ArrayList<String> arraydirection= new ArrayList<String>();
        arraydirection.add("asc");
        arraydirection.add("desc");

        String sortBy = req.getParameter("sortBy");
        String direction = req.getParameter("direction");

        if(tag == null || tag.isEmpty())
            throw new TagNotFound("Tags parameter is required");


        if(direction == null || !arraydirection.contains(direction) || direction!="")
            if(direction == null || direction.equalsIgnoreCase(""))
                direction="asc";
            else if(!arraydirection.contains(direction) )
                throw new DirectionSortByError("Direction parameter is invalid");

        if(sortBy == null || !arraysortBy.contains(sortBy) || sortBy.equalsIgnoreCase(""))
            if(sortBy == null || sortBy.equalsIgnoreCase(""))
                sortBy = "id";
            else if (!arraysortBy.contains(""+sortBy))
                throw new DirectionSortByError("sortBy parameter is invalid");

        for (String filter:tag){
            try {
                allFutures.add(postClient.getPostsByTag(filter));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
                CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();

        for (int i=0;i< allFutures.size();i++){
            try {

                for (Post post: allFutures.get(i).get().getPosts()){
                    if(!posts.contains(post)){
                        posts.add(post);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if(sortBy.equalsIgnoreCase("id")){
            Collections.sort(posts);
        }else if(sortBy.equalsIgnoreCase("likes")){
            if(direction.equalsIgnoreCase("asc")){
                posts.sort(Comparator.comparing(p->p.getLikes()));
            }else{
                Comparator<Post> comparator = Comparator.comparing(e -> e.getLikes());
                posts.sort(comparator.reversed());
            }
        }else if(sortBy.equalsIgnoreCase("popularity")){
            if(direction.equalsIgnoreCase("asc")){
                posts.sort(Comparator.comparing(p->p.getPopularity()));
            }else{
                Comparator<Post> comparator = Comparator.comparing(e -> e.getPopularity());
                posts.sort(comparator.reversed());
            }
        }else if(sortBy.equalsIgnoreCase("reads")){
            if(direction.equalsIgnoreCase("asc")){
                posts.sort(Comparator.comparing(p->p.getReads()));
            }else{
                Comparator<Post> comparator = Comparator.comparing(e -> e.getReads());
                posts.sort(comparator.reversed());
            }
        }
        posts = posts.stream().distinct().collect(Collectors.toList());
        postBean.setPosts(posts);
        return postBean;
    }
}
