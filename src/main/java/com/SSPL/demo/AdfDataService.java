package com.SSPL.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdfDataService {

    @Autowired
    private AdfDataRepository adfDataRepository;

    public void saveData(database adfData) {
        adfDataRepository.save(adfData);
    }
}
