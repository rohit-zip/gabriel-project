package com.gn128.processor.listprocessor;

import com.gn128.constants.ServiceConstants;
import com.gn128.enums.AbsoluteFilterKeyType;
import com.gn128.enums.KeyType;
import com.gn128.enums.Operator;
import com.gn128.enums.Order;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.ListDataPayloads.*;
import com.gn128.payloads.ListPayload;
import com.gn128.payloads.ProfileListProvider;
import com.gn128.properties.ProfileListProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.processor.listprocessor
 * Created_on - December 04 - 2024
 * Created_at - 19:44
 */

@Component
@RequiredArgsConstructor
public class ProfileListProcessor {

    private final ProfileListProperties properties;
    private final ProfileListProperties profileListProperties;

    public ListPayload initProcess(ListPayload listRequest) {
        ListPayload request = new ListPayload();
        request.setFilters(addFilter(listRequest));
        request.setExcludeFilter(listRequest.getExcludeFilter());
        request.setAbsoluteFilters(addAbsoluteFilter(listRequest));
        request.setSearchFilters(addSearchFilter());
        request.setSorts(addSort(listRequest));
        request.setSearch(listRequest.getSearch());
        request.setPage(listRequest.getPage());
        request.setSize(listRequest.getSize());
        return request;
    }

    private List<Filter> addFilter(ListPayload listPayload) {
        Map<String, ProfileListProvider> profileListData = properties.getData();
        List<Filter> filters = new ArrayList<>();
        if (!CollectionUtils.isEmpty(listPayload.getFilters())) {
            listPayload
                    .getFilters()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(filter -> {
                        String columnName = null;
                        Boolean partialSearch = true;
                        String filterKeyType = null;
                        if (profileListData.containsKey(filter.getFilterKey())) {
                            ProfileListProvider value = profileListData.get(filter.getFilterKey());
                            columnName = value.getFilterAndSortField();
                            partialSearch = isPartialSearch(value);
                            filterKeyType = value.getType();
                        } else {
                            throw new BadRequestException("Filter Key is not valid", HttpStatus.BAD_REQUEST);
                        }
                        Filter f = new Filter();
                        f.setFilterKey(columnName);
                        if (KeyType.getInstance(filterKeyType) == null) {
                            throw new BadRequestException("Filter Key Type is not valid", HttpStatus.BAD_REQUEST);
                        }
                        f.setFilterKeyType(KeyType.getInstance(filterKeyType));
                        List<String> selections = new ArrayList<>();
                        if (Objects.nonNull(filter.getSelections())) {
                            filter
                                    .getSelections()
                                    .stream()
                                    .filter(Objects::nonNull)
                                    .forEach(data -> selections.add(data.toString()));
                        }
                        f.setSelections(selections);
                        f.setPartialSearch(partialSearch);
                        filters.add(f);
                    });
        }
        return filters;
    }

    private Boolean isPartialSearch(ProfileListProvider profileListProvider) {
        if (profileListProvider.getType().equals(ServiceConstants.STRING)) {
            return profileListProvider.getPartialSearch();
        } else {
            return Boolean.FALSE;
        }
    }

    private List<AbsoluteFilter> addAbsoluteFilter(ListPayload listPayload) {
        Map<String, ProfileListProvider> profileListData = properties.getData();
        List<AbsoluteFilter> filters = new ArrayList<>();
        if (!CollectionUtils.isEmpty(listPayload.getAbsoluteFilters())) {
            listPayload
                    .getAbsoluteFilters()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(absoluteFilter -> {
                        String columnName = null;
                        String filterKeyType = null;
                        if (profileListData.containsKey(absoluteFilter.getFilterKey())) {
                            ProfileListProvider value = profileListData.get(absoluteFilter.getFilterKey());
                            columnName = value.getFilterAndSortField();
                            filterKeyType = value.getType();
                        } else {
                            throw new BadRequestException("Absolute Filter Key is not valid", HttpStatus.BAD_REQUEST);
                        }
                        filters.add(getAbsoluteFilter(absoluteFilter, columnName, filterKeyType));
                    });
        }
        return filters;
    }

    private AbsoluteFilter getAbsoluteFilter(AbsoluteFilter absoluteFilter, String columnName, String filterKeyType) {
        AbsoluteFilter filter = new AbsoluteFilter();
        filter.setFilterKey(columnName);
        if (AbsoluteFilterKeyType.getInstance(filterKeyType)==null) {
            throw new BadRequestException("Absolute Filter Key Type is not valid", HttpStatus.BAD_REQUEST);
        }
        if ((AbsoluteFilterKeyType.DATE_TIME).equals(AbsoluteFilterKeyType.getInstance(filterKeyType)) && !ObjectUtils.isEmpty(absoluteFilter.getOperator())) {
            throw new BadRequestException("Absolute Filter Key Operator is not valid", HttpStatus.BAD_REQUEST);
        }
        if ((Operator.NOT_EQUAL).equals(absoluteFilter.getOperator()) && !ObjectUtils.isEmpty(absoluteFilter.getSelection().getMaximum())) {
            throw new BadRequestException("Absolute Filter Selection Data is not valid", HttpStatus.BAD_REQUEST);
        }
        filter.setFilterKeyType(AbsoluteFilterKeyType.getInstance(filterKeyType));
        AbsoluteFilterSelection absoluteFilterSelection = new AbsoluteFilterSelection();
        if (absoluteFilter.getSelection()!=null) {
            absoluteFilterSelection.setMaximum(absoluteFilter.getSelection().getMaximum());
            absoluteFilterSelection.setMinimum(absoluteFilter.getSelection().getMinimum());
        }
        filter.setSelection(absoluteFilterSelection);
        filter.setOperator(absoluteFilter.getOperator());
        return filter;
    }

    private List<SearchFilter> addSearchFilter() {
        Map<String, ProfileListProvider> profileListData = properties.getData();
        List<SearchFilter> searchFilters = new ArrayList<>();
        profileListData
                .forEach((key, value) -> {
                    if (Boolean.TRUE.equals(value.getSearchAllowed())) {
                        searchFilters.add(
                                getSearchFilter(
                                        value.getSearchField(),
                                        isPartialSearch(value)
                                )
                        );
                    }
                });
        return searchFilters;
    }

    private SearchFilter getSearchFilter(String field, Boolean isPartialSearch) {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setField(field);
        searchFilter.setPartialSearch(isPartialSearch);
        return searchFilter;
    }

    private List<Sort> addSort(ListPayload listPayload) {
        List<Sort> sorts = new ArrayList<>();
        Map<String, ProfileListProvider> profileListData = profileListProperties.getData();
        if (!CollectionUtils.isEmpty(listPayload.getSorts())) {
            listPayload
                    .getSorts()
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(sort -> {
                        String columnName = null;
                        String fieldType = null;
                        if (profileListData.containsKey(sort.getKey())) {
                            ProfileListProvider data = profileListData.get(sort.getKey());
                            columnName = data.getFilterAndSortField();
                            fieldType = data.getType();
                        } else {
                            throw new BadRequestException("Sort Key is not valid", HttpStatus.BAD_REQUEST);
                        }
                        sorts.add(getSort(sort.getOrder().name(), columnName, fieldType));
                    });
        } else {
            sorts.add(getSortByDateCreated(ServiceConstants.DESCENDING, ServiceConstants.DATE_CREATED));
        }
        return sorts;
    }

    private Sort getSort(String order, String columnName, String fieldType) {
        Sort sort = new Sort();
        sort.setOrder(Order.valueOf(order));
        sort.setKey(columnName);
        sort.setFieldType(fieldType);
        return sort;
    }

    private Sort getSortByDateCreated(String order, String columnName) {
        Sort sort = new Sort();
        sort.setOrder(Order.valueOf(order));
        sort.setKey(columnName);
        sort.setFieldType("DATE_TIME");
        return sort;
    }
}
