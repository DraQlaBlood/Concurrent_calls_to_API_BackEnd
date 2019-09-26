package com.hatchways.backEnd;


import com.hatchways.backEnd.dao.PostClient;
import com.hatchways.backEnd.exceptions.DirectionSortByError;
import com.hatchways.backEnd.exceptions.TagNotFound;
import com.hatchways.backEnd.model.Post;
import com.hatchways.backEnd.model.PostBean;
import com.hatchways.backEnd.web.Controller;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public   class BackEndApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private MockMvc mvc;


	@Test
	public void test_Endpoint_posts() throws Exception{
		mvc.perform(
				MockMvcRequestBuilders.get("/api/posts")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is4xxClientError());
	}

	@Test
	public void test_Endpoint_ping() throws Exception{
		mvc.perform(
				MockMvcRequestBuilders.get("/api/ping")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
	}

	@Test
	public void test_Endpoint_posts_SingleTag_Attribute() throws Exception{
		mvc.perform(
				MockMvcRequestBuilders.get("/api/posts?tags=tech")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void test_Endpoint_posts_MultipleTag_Attribute() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/api/posts?tags=tech,history")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void test_Endpoint_posts_MultipleTag_Sorting_Attribute() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/api/posts?tags=tech,history&sortBy=likes&direction=desc")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	public void test_Endpoint_posts_MultipleTag_WrongDirection_Attribute() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/api/posts?tags=tech,history&sortBy=likes&direction=auto")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void test_Endpoint_posts_MultipleTag_WrongSorting_Attribute() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/api/posts?tags=tech,history&sortBy=like&direction=desc")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void test_Endpoint_posts_WithEmptyTag_Attribute() throws Exception{
		mvc.perform(
				MockMvcRequestBuilders.get("/api/posts?tags=")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void test_PostClient_Value_Returned()throws Exception{

		PostBean postBean = new PostBean();
		postBean.setPosts(new PostClient()
				.getPostsByTag("tech").get().getPosts());

		mvc.perform(MockMvcRequestBuilders.get("/api/posts?tags=tech")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.posts[0].id").value(postBean.getPosts().get(0).getId()));

	}
}
