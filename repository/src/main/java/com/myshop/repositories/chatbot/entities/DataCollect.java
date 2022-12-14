package com.myshop.repositories.chatbot.entities;

import com.myshop.repositories.Auditing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "data_collected")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataCollect extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(255)")
    private String request;

    @Column(columnDefinition = "nvarchar(255)")
    private String response;

}
