package org.springframework.data.jpa.datatables;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;

import java.util.ArrayList;
import java.util.List;

public class PredicateBuilder extends AbstractPredicateBuilder<Predicate> {
    private final PathBuilder<?> entity;
    private List<Predicate> columnPredicates = new ArrayList<>();
    private List<Predicate> globalPredicates = new ArrayList<>();

    public PredicateBuilder(PathBuilder<?> entity, DataTablesInput input) {
        super(input);
        this.entity = entity;
    }

    @Override
    public Predicate build() {
        initPredicatesRecursively(tree, entity);

        return createFinalPredicate();
    }

    private void initPredicatesRecursively(Node<Filter> node, PathBuilder<?> pathBuilder) {
        if (node.isLeaf()) {
            boolean hasColumnFilter = node.getData() != null;
            if (hasColumnFilter) {
                Filter columnFilter = node.getData();
                columnPredicates.add(columnFilter.createPredicate(pathBuilder, node.getName()));
            } else if (hasGlobalFilter) {
                Filter globalFilter = tree.getData();
                globalPredicates.add(globalFilter.createPredicate(pathBuilder, node.getName()));
            }
        }
        for (Node<Filter> child : node.getChildren()) {
            initPredicatesRecursively(child, child.isLeaf() ? pathBuilder : pathBuilder.get(child.getName()));
        }
    }

    private Predicate createFinalPredicate() {
        BooleanBuilder predicate = new BooleanBuilder();

        for (Predicate columnPredicate : columnPredicates) {
            predicate = predicate.and(columnPredicate);
        }

        if (!globalPredicates.isEmpty()) {
            predicate = predicate.andAnyOf(globalPredicates.toArray(new Predicate[0]));
        }

        return predicate;
    }

}