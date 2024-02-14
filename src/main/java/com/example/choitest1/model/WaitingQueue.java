package com.example.choitest1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "TB_WAITING")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaitingQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long schoolSeq;

    private long userSeq;

    private int position;

}
