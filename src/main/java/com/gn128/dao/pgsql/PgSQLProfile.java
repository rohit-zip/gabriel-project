package com.gn128.dao.pgsql;

import com.gn128.constants.ServiceConstants;
import com.gn128.dao.CriteriaQueryGenerator;
import com.gn128.entity.Profile;
import com.gn128.enums.Operator;
import com.gn128.exception.payloads.BadRequestException;
import com.gn128.payloads.ListDataPayloads.AbsoluteFilter;
import com.gn128.payloads.ListDataPayloads.ExcludeFilter;
import com.gn128.payloads.ListPayload;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.gn128.constants.ServiceConstants.DATE_FORMAT;
import static com.gn128.constants.ServiceConstants.STRING;
import static com.gn128.enums.AbsoluteFilterKeyType.DATE_TIME;
import static com.gn128.enums.Order.ASC;

/**
 * Author - rohit
 * Project - java-backend
 * Package - com.gn128.dao.pgsql
 * Created_on - December 04 - 2024
 * Created_at - 18:56
 */

@Component(ServiceConstants.PROFILE_PGSQL_BUILDER_COMPONENT)
public class PgSQLProfile implements CriteriaQueryGenerator<Profile> {

    @PersistenceContext
    private EntityManager entityManager;

    public TypedQuery<Profile> build(ListPayload listPayload) {
        CriteriaQuery<Profile> query = getCriteriaBuilder().createQuery(Profile.class);
        Root<Profile> root = query.from(Profile.class);
        return getQuery(listPayload, query, root);
    }

    private TypedQuery<Profile> getQuery(ListPayload listRequest, CriteriaQuery<Profile> query, Root<Profile> root) {
        List<Predicate> predicates = getPredicatesList(listRequest, root);
        query
                .select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(initSort(listRequest, root));
        return entityManager
                .createQuery(query)
                .setFirstResult(listRequest.getPage() * listRequest.getSize())
                .setMaxResults(listRequest.getSize());
    }

    private CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    private List<Predicate> getPredicatesList(ListPayload listRequest, Root<Profile> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.addAll(initSearch(listRequest, root));
        predicates.addAll(initFilter(listRequest, root));
        predicates.addAll(initAbsoluteFilter(listRequest, root));
        predicates.addAll(initExcludeFilter(listRequest, root));
        return predicates;
    }

    @Override
    public List<Predicate> initSearch(ListPayload listPayload, Root<Profile> root) {
        List<Predicate> searchPredicates = new ArrayList<>();
        if (!ObjectUtils.isEmpty(listPayload.getSearch())) {
            List<Predicate> textPredicates = new ArrayList<>();
            listPayload
                    .getSearch()
                    .stream()
                    .filter(data -> !ObjectUtils.isEmpty(data))
                    .forEach(text -> {
                        List<Predicate> fieldPredicate = new ArrayList<>();
                        listPayload
                                .getSearchFilters()
                                .forEach(searchFilter -> {
                                    if (searchFilter.isPartialSearch()) {
                                        fieldPredicate
                                                .add(getCriteriaBuilder().like(getCriteriaBuilder().lower(root.get(searchFilter.getField())),
                                                        "%" + text.toLowerCase() + "%"));
                                    }
                                });
                        textPredicates.add(getCriteriaBuilder().or(fieldPredicate.toArray(new Predicate[0])));
                    });
            searchPredicates.add(getCriteriaBuilder().and(textPredicates.toArray(new Predicate[0])));
        }
        return searchPredicates;
    }

    @Override
    public List<Order> initSort(ListPayload listPayload, Root<Profile> root) {
        List<Order> order = new ArrayList<>();
        if (!ObjectUtils.isEmpty(listPayload.getSorts())) {
            listPayload
                    .getSorts()
                    .forEach(sort -> {
                        if (ASC.equals(sort.getOrder())) {
                            if (STRING.equalsIgnoreCase(sort.getFieldType())) {
                                order.add(getCriteriaBuilder().asc(getCriteriaBuilder().upper(root.get(sort.getKey()))));
                            } else {
                                order.add(getCriteriaBuilder().asc(root.get(sort.getKey())));
                            }
                        } else {
                            if (STRING.equalsIgnoreCase(sort.getFieldType())) {
                                order.add(getCriteriaBuilder().desc(getCriteriaBuilder().upper(root.get(sort.getKey()))));
                            } else {
                                order.add(getCriteriaBuilder().desc(root.get(sort.getKey())));
                            }
                        }
                    });
        }
        return order;
    }

    @Override
    public List<Predicate> initFilter(ListPayload listRequest, Root<Profile> root) {
        List<Predicate> filterPredicates = new ArrayList<>();
        if (!ObjectUtils.isEmpty(listRequest.getFilters())) {
            listRequest
                    .getFilters()
                    .forEach(filter -> {
                        List<Predicate> selectionPredicates = new ArrayList<>();
                        if (filter.isPartialSearch()) {
                            filter
                                    .getSelections()
                                    .forEach(selection ->
                                            selectionPredicates
                                                    .add(getCriteriaBuilder()
                                                            .like(getCriteriaBuilder()
                                                                            .lower(root.get(filter.getFilterKey())),
                                                                    "%" + selection.toString().toLowerCase() + "%"))
                                    );
                            filterPredicates
                                    .add(
                                            getCriteriaBuilder()
                                                    .or(selectionPredicates.toArray(new Predicate[0]))
                                    );
                        } else {
                            filterPredicates
                                    .add(root
                                            .get(filter.getFilterKey())
                                            .in(filter.getSelections())
                                    );
                        }
                    });
        }
        return filterPredicates;
    }

    @Override
    public List<Predicate> initAbsoluteFilter(ListPayload listRequest, Root<Profile> root) {
        List<Predicate> absoluteFilterPredicate = new ArrayList<>();
        if (!ObjectUtils.isEmpty(listRequest.getAbsoluteFilters())) {
            listRequest
                    .getAbsoluteFilters()
                    .forEach(absoluteFilter -> {
                        List<Predicate> selectionPredicates = new ArrayList<>();
                        selectionPredicates = setMaxValue(absoluteFilter, selectionPredicates, root);
                        selectionPredicates = setMinValue(absoluteFilter, selectionPredicates, root);
                        Predicate[] predicatesArray = selectionPredicates.toArray(new Predicate[0]);
                        absoluteFilterPredicate
                                .add(getCriteriaBuilder().and(predicatesArray));
                    });
        }
        return absoluteFilterPredicate;
    }

    @Override
    public List<Predicate> initExcludeFilter(ListPayload listPayload, Root<Profile> root) {
        List<Predicate> excludeFilterPredicates = new ArrayList<>();
        ExcludeFilter excludeFilter = listPayload.getExcludeFilter();
        if (!ObjectUtils.isEmpty(excludeFilter)) {
            Predicate excludePredicate = getCriteriaBuilder()
                    .not(root.get(excludeFilter.getFilterKey()).in(excludeFilter.getSelections()));
            excludeFilterPredicates.add(excludePredicate);
        }
        return excludeFilterPredicates;
    }

    private List<Predicate> setMaxValue(AbsoluteFilter absoluteFilter, List<Predicate> selectionPredicates, Root<Profile> root) {
        if (!ObjectUtils.isEmpty(absoluteFilter.getSelection().getMaximum())) {
            if (ObjectUtils.isEmpty(absoluteFilter.getOperator()) || Operator.EQUAL_TO.equals(absoluteFilter.getOperator())) {
                if (DATE_TIME.equals(absoluteFilter.getFilterKeyType())) {
                    selectionPredicates
                            .add(getCriteriaBuilder()
                                    .lessThanOrEqualTo(root.get(absoluteFilter.getFilterKey()), getDate(absoluteFilter.getSelection().getMaximum())));
                } else {
                    selectionPredicates.add(getCriteriaBuilder().lessThanOrEqualTo(
                            root.get(absoluteFilter.getFilterKey()), absoluteFilter.getSelection().getMaximum()
                    ));
                }
            } else if (Operator.NOT_EQUAL_TO.equals(absoluteFilter.getOperator())) {
                if (DATE_TIME.equals(absoluteFilter.getFilterKeyType())) {
                    selectionPredicates.add(getCriteriaBuilder().lessThan(
                            root.get(absoluteFilter.getFilterKey()), getDate(absoluteFilter.getSelection().getMaximum())
                    ));
                } else {
                    selectionPredicates.add(getCriteriaBuilder().lessThan(
                            root.get(absoluteFilter.getFilterKey()), absoluteFilter.getSelection().getMaximum()
                    ));
                }
            }
        }
        return selectionPredicates;
    }

    private List<Predicate> setMinValue(AbsoluteFilter absoluteFilter, List<Predicate> selectionPredicates, Root<Profile> root) {
        if (!ObjectUtils.isEmpty(absoluteFilter.getSelection().getMinimum())) {
            if (ObjectUtils.isEmpty(absoluteFilter.getOperator()) || Operator.EQUAL_TO.equals(absoluteFilter.getOperator())) {
                if (DATE_TIME.equals(absoluteFilter.getFilterKeyType())) {
                    selectionPredicates
                            .add(getCriteriaBuilder()
                                    .greaterThanOrEqualTo(root.get(absoluteFilter.getFilterKey()), getDate(absoluteFilter.getSelection().getMinimum())));
                } else {
                    selectionPredicates.add(getCriteriaBuilder().greaterThanOrEqualTo(
                            root.get(absoluteFilter.getFilterKey()), absoluteFilter.getSelection().getMinimum()
                    ));
                }
            } else if (Operator.NOT_EQUAL_TO.equals(absoluteFilter.getOperator())) {
                if (DATE_TIME.equals(absoluteFilter.getFilterKeyType())) {
                    selectionPredicates.add(getCriteriaBuilder().greaterThan(
                            root.get(absoluteFilter.getFilterKey()), getDate(absoluteFilter.getSelection().getMinimum())
                    ));
                } else {
                    selectionPredicates.add(getCriteriaBuilder().greaterThan(
                            root.get(absoluteFilter.getFilterKey()), absoluteFilter.getSelection().getMinimum()
                    ));
                }
            } else if (Operator.NOT_EQUAL.equals(absoluteFilter.getOperator())) {
                selectionPredicates.add(getCriteriaBuilder().notEqual(
                        root.get(absoluteFilter.getFilterKey()), absoluteFilter.getSelection().getMinimum()
                ));
            }
        }
        return selectionPredicates;
    }

    private Date getDate(String value) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            return simpleDateFormat.parse(value);
        } catch (ParseException exception) {
            throw new BadRequestException("Date is not of Correct format. Format should be of type (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')", HttpStatus.BAD_REQUEST);
        }
    }

    public Long getTotalRecords(ListPayload listRequest) {
        CriteriaQuery<Long> query = getCriteriaBuilder().createQuery(Long.class);
        Root<Profile> root = query.from(Profile.class);
        List<Predicate> predicates = getPredicatesList(listRequest, root);
        query.select(getCriteriaBuilder().count(root)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getSingleResult();
    }
}
