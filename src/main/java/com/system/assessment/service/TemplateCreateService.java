package com.system.assessment.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface TemplateCreateService {

    public String selfReport();

    public File updateWordFile(String name,Map<String, String> variables)throws IOException;

    public  File generateWordFile(String name, Double score) throws IOException;
}
