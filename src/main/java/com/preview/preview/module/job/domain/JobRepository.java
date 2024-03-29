package com.preview.preview.module.job.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
    Job findByName(String name);
}
