package com.nphc.service;

import org.springframework.stereotype.Component;

import com.nphc.helper.SearchCriteria;
import com.nphc.helper.SearchOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchCriteriaParser {

    private static final String wordRegex = "[a-zA-Z]\\w*";
    private static final String valueRegex = "\\w+";
    private static final String operatorRegex = "(:|<|>|<=|>=|%|!|\\+|-|\\s)";
    private static final String timestampRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
    private static final String idRegex = "\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}";
    private static final String regex = "[0-9]*['.']?[0-9]*";
    private static final String fullRegex = "(" + wordRegex + ")" + operatorRegex + "(" + timestampRegex + "|" + idRegex
            + "|" + valueRegex + "|" + regex + ")?,";
    private static final Pattern searchPattern = Pattern.compile(fullRegex);

    public List<SearchCriteria> parse(String searchString) {
        List<SearchCriteria> searchCriterias = new ArrayList<>();
        if (searchString != null) {
            Matcher matcher = searchPattern.matcher(searchString + ",");
            while (matcher.find()) {
                SearchCriteria searchCriteria = new SearchCriteria();
                searchCriteria.setKey(matcher.group(1));
                searchCriteria.setOperation(SearchOperation.getSimpleOperation(matcher.group(2)));
                searchCriteria.setValue(matcher.group(3));
                // easiest way to test for bad UUIDs
                if ((searchCriteria.getOperation() != SearchOperation.SORT_DESC && searchCriteria.getOperation() != SearchOperation.SORT_ASC) || searchCriteria.getValue() == null) {
                    searchCriterias.add(searchCriteria);
                }
            }
        }
        return searchCriterias;
    }
}
