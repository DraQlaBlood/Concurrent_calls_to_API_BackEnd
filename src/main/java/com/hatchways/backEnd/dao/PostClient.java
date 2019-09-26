package com.hatchways.backEnd.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hatchways.backEnd.model.Post;
import com.hatchways.backEnd.model.PostBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PostClient {
    RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<PostBean> getPostsByTag(String tagName) throws  JSONException {
        String url = "https://hatchways.io/api/assessment/blog/posts?tag="+tagName;

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        PostBean allPosts = new PostBean();
        List<Post> allPost = new ArrayList<Post>();
        JSONObject root = new JSONObject(String.valueOf(response));

        JSONArray posts = root.getJSONArray("posts");

        for(int i=0;i< posts.length();i++){

            JSONObject jsonPost =  posts.getJSONObject(i);
            Post post = new Post();
            List<String> tag = new ArrayList<>();
            post.setAuthor(jsonPost.getString("author"));
            post.setAuthorId(jsonPost.getInt("authorId"));
            post.setId(jsonPost.getInt("id"));
            post.setLikes(jsonPost.getInt("likes"));
            post.setPopularity(jsonPost.getDouble("popularity"));
            post.setReads(jsonPost.getInt("reads"));
            for (int j=0; j<jsonPost.getJSONArray("tags").length();j++){
                String str =  jsonPost.getJSONArray("tags").getString(j);
                tag.add(str);
            }

            post.setTags(tag);

            if(tag.contains(tagName)){
                allPost.add(post);
            }
        }

        Collections.sort(allPost);

        allPosts.setPosts(allPost);

        return  CompletableFuture.completedFuture(allPosts);
    }



}
