package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSimulator {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String deviceId;
    private Date lastUpdated;
    private Long messageLog;

}
