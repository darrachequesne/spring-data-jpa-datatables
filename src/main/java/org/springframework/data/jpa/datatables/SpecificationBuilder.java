package org.springframework.data.jpa.datatables;

import org.hibernate.query.criteria.internal.path.AbstractPathImpl;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


public class SpecificationBuilder<T> extends AbstractPredicateBuilder<Specification<T>> {
    public SpecificationBuilder(DataTablesInput input) {
        super(input);
    }

    @Override
    public Specification<T> build() {
        return new DataTablesSpecification<T>();
    }

    private class DataTablesSpecification<S> implements Specification<S> {
        private List<Predicate> columnPredicates = new ArrayList<Predicate>();
        private List<Predicate> globalPredicates = new ArrayList<Predicate>();

        @Override
        public Predicate toPredicate(Root<S> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            initPredicatesRecursively(tree, root, root, criteriaBuilder);

            boolean isCountQuery = query.getResultType() == Long.class;
            if (isCountQuery) {
                root.getFetches().clear();
            }

            return createFinalPredicate(criteriaBuilder);
        }

        private void initPredicatesRecursively(Node<Filter> node, From<S, S> from, FetchParent<S, S> fetch, CriteriaBuilder criteriaBuilder) {
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
                if (path instanceof AbstractPathImpl) {
                    if (((AbstractPathImpl) path).getAttribute().isCollection()) {
                        // ignore OneToMany and ManyToMany relationships
                        continue;
                    }
                }
                if (child.isLeaf()) {
                    initPredicatesRecursively(child, from, fetch, criteriaBuilder);
                } else {
                    Join<S, S> join = from.join(child.getName(), JoinType.LEFT);
                    Fetch<S, S> childFetch = fetch.fetch(child.getName(), JoinType.LEFT);
                    initPredicatesRecursively(child, join, childFetch, criteriaBuilder);
                }
            }
        }

        private Predicate createFinalPredicate(CriteriaBuilder criteriaBuilder) {
            List<Predicate> allPredicates = new ArrayList<Predicate>(columnPredicates);

            if (!globalPredicates.isEmpty()) {
                allPredicates.add(criteriaBuilder.or(globalPredicates.toArray(new Predicate[0])));
            }

            return allPredicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(allPredicates.toArray(new Predicate[0]));
        }
    }

}