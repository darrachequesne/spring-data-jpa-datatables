package org.springframework.data.jpa.datatables;

import jakarta.persistence.metamodel.Bindable.BindableType;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class SpecificationBuilder<T> extends AbstractPredicateBuilder<Specification<T>> {
    public SpecificationBuilder(DataTablesInput input) {
        super(input);
    }

    @Override
    public Specification<T> build() {
        return new DataTablesSpecification<>();
    }

    private class DataTablesSpecification<S> implements Specification<S> {
        @Override
        public Predicate toPredicate(@NonNull Root<S> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
            Predicates predicates = new Predicates();
            initPredicatesRecursively(predicates, query, tree, root, root, criteriaBuilder);

            if (input.getSearchPanes() != null) {
                input.getSearchPanes().forEach((attribute, values) -> {
                    if (!values.isEmpty()) {
                        predicates.columns.add(root.get(attribute).in(values));
                    }
                });
            }

            return predicates.toPredicate(criteriaBuilder);
        }

        private static boolean isCountQuery(CriteriaQuery<?> query) {
            return query.getResultType() == Long.class;
        }

        private static boolean isAggregateQuery(CriteriaQuery<?> query) {
            return query.getGroupList().size() > 0;
        }

        private void initPredicatesRecursively(Predicates predicates, CriteriaQuery<?> query, Node<Filter> node, From<S, S> from, FetchParent<S, S> fetch, CriteriaBuilder criteriaBuilder) {
            if (node.isLeaf()) {
                boolean hasColumnFilter = node.getData() != null;
                if (hasColumnFilter) {
                    Filter columnFilter = node.getData();
                    predicates.columns.add(columnFilter.createPredicate(from, criteriaBuilder, node.getName()));
                } else if (hasGlobalFilter) {
                    Filter globalFilter = tree.getData();
                    predicates.global.add(globalFilter.createPredicate(from, criteriaBuilder, node.getName()));
                }
            }
            for (Node<Filter> child : node.getChildren()) {
                Path<Object> path = from.get(child.getName());
                if (path.getModel().getBindableType() == BindableType.PLURAL_ATTRIBUTE) {
                    // ignore OneToMany and ManyToMany relationships
                    continue;
                }
                if (child.isLeaf()) {
                    initPredicatesRecursively(predicates, query, child, from, fetch, criteriaBuilder);
                } else {
                    Join<S, S> join = from.join(child.getName(), JoinType.LEFT);

                    if (isCountQuery(query) || isAggregateQuery(query)) {
                        initPredicatesRecursively(predicates, query, child, join, join, criteriaBuilder);
                    } else {
                        Fetch<S, S> childFetch = fetch.fetch(child.getName(), JoinType.LEFT);
                        initPredicatesRecursively(predicates, query, child, join, childFetch, criteriaBuilder);
                    }
                }
            }
        }
    }

    private static class Predicates {
        public List<Predicate> columns = new ArrayList<>();
        public List<Predicate> global = new ArrayList<>();

        Predicate toPredicate(CriteriaBuilder criteriaBuilder) {
            if (!global.isEmpty()) {
                columns.add(criteriaBuilder.or(global.toArray(new Predicate[0])));
            }

            return columns.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(columns.toArray(new Predicate[0]));
        }
    }
}