/*
 * Copyright 2023 Rohit Parihar and Bloggios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gn128.dao;

import com.gn128.payloads.ListPayload;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface CriteriaQueryGenerator<A> {

    List<Predicate> initSearch(ListPayload listPayload, Root<A> root);
    List<Order> initSort(ListPayload listPayload, Root<A> root);
    List<Predicate> initFilter(ListPayload listPayload, Root<A> root);
    List<Predicate> initAbsoluteFilter(ListPayload listPayload, Root<A> root);
    List<Predicate> initExcludeFilter(ListPayload listPayload, Root<A> root);
    //List<Predicate> initDistanceFilter(ListPayload listPayload, Root<A> root);
}
