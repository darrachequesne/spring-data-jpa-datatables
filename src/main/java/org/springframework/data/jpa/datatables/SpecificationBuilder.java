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
        protected List<Predicate> columnPredicates = new ArrayList<>();
        protected List<Predicate> globalPredicates = new ArrayList<>();

        @Override
        public Predicate toPredicate(@NonNull Root<S> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
            initPredicatesRecursively(query, tree, root, root, criteriaBuilder);

            if (input.getSearchPanes() != null) {
                input.getSearchPanes().forEach((attribute, values) -> {
                    if (!values.isEmpty()) {
                        columnPredicates.add(root.get(attribute).in(values));
                    }
                });
            }

            final Predicate predicate = createFinalPredicate(criteriaBuilder);
            columnPredicates.clear();
            return predicate;
        }

        private boolean isCountQuery(CriteriaQuery<?> query) {
            return query.getResultType() == Long.class;
        }

        protected void initPredicatesRecursively(CriteriaQuery<?> query, Node<Filter> node, From<S, S> from, FetchParent<S, S> fetch, CriteriaBuilder criteriaBuilder) {
            if (node.isLeaf()) {
                boolean hasColumnFilter = node.getData() != null;
                if (hasColumnFilter) {
                    Filter columnFilter = node.getData();
                    columnPredicates.add(columnFilter.createPredicate(from, criteriaBuilder, node.getName()));
                } else if (hasGlobalFilter) {
                    Filter globalFilter = tree.getData();
                    globalPredicates.add(globalFilter.createPredicate(from, criteriaBuilder, node.getName()));
                }
            }
            for (Node<Filter> child : node.getChildren()) {
                Path<Object> path = from.get(child.getName());
                if (path.getModel().getBindableType() == BindableType.PLURAL_ATTRIBUTE) {
                    // ignore OneToMany and ManyToMany relationships
                    continue;
                }
                if (child.isLeaf()) {
                    initPredicatesRecursively(query, child, from, fetch, criteriaBuilder);
                } else {
                    Join<S, S> join = from.join(child.getName(), JoinType.LEFT);

                    if (isCountQuery(query)) {
                        initPredicatesRecursively(query, child, join, join, criteriaBuilder);
                    } else {
                        Fetch<S, S> childFetch = fetch.fetch(child.getName(), JoinType.LEFT);
                        initPredicatesRecursively(query, child, join, childFetch, criteriaBuilder);
                    }
                }
            }
        }

        protected Predicate createFinalPredicate(CriteriaBuilder criteriaBuilder) {
            List<Predicate> allPredicates = new ArrayList<>(columnPredicates);

            if (!globalPredicates.isEmpty()) {
                allPredicates.add(criteriaBuilder.or(globalPredicates.toArray(new Predicate[0])));
            }

            return allPredicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(allPredicates.toArray(new Predicate[0]));
        }
    }

    private class DataTablesSearchPaneSpecification<S> extends DataTablesSpecification<S> {

        @Override
        protected void initPredicatesRecursively(CriteriaQuery<?> query, Node<Filter> node, From<S, S> from,
            FetchParent<S, S> fetch, CriteriaBuilder criteriaBuilder) {
            if (node.isLeaf()) {
                boolean hasColumnFilter = node.getData() != null;
                if (hasColumnFilter) {
                    Filter columnFilter = node.getData();
                    columnPredicates.add(columnFilter.createPredicate(from, criteriaBuilder, node.getName()));
                } else if (hasGlobalFilter) {
                    Filter globalFilter = tree.getData();
                    globalPredicates.add(globalFilter.createPredicate(from, criteriaBuilder, node.getName()));
                }
            }
            for (Node<Filter> child : node.getChildren()) {
                Path<Object> path = from.get(child.getName());
                if (path.getModel().getBindableType() == BindableType.PLURAL_ATTRIBUTE) {
                    // ignore OneToMany and ManyToMany relationships
                    continue;
                }
                if (child.isLeaf()) {
                    initPredicatesRecursively(query, child, from, fetch, criteriaBuilder);
                } else {
                    Join<S, S> join = from.join(child.getName(), JoinType.LEFT);
                    initPredicatesRecursively(query, child, join, fetch, criteriaBuilder);
                }
            }
        }
    }

    public Specification<T> buildSearchPane() {
        return new DataTablesSearchPaneSpecification<>();
    }
}