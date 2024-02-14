package com.example.choitest1.repo;

import com.example.choitest1.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepo extends JpaRepository<School, Long> {


    School findAllBySchoolSeq(long schoolSeq);


}
