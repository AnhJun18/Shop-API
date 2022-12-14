package com.myshop.api.controller;


import com.myshop.repositories.chatbox.repos.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Anh Jun
 * @author Anh Jun
 */

@RestController
@RequestMapping("/api/chatbot")
public class ChatBoxController {
    @Autowired
    TagRepository tagRepository;

}
