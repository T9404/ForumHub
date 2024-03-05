package com.example.core.message;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FilterMessageRepositoryImpl implements FilterMessageRepository {
    private final EntityManager entityManager;

    @Override
    public List<MessageEntity> findAll(MessageFilter filter, PageRequest pageRequest) {
        var builder = entityManager.getCriteriaBuilder();
        var criteria = builder.createQuery(MessageEntity.class);
        var message = criteria.from(MessageEntity.class);
        criteria.select(message);

        List<Predicate> predicates = buildPredicates(filter, builder, message);
        criteria.where(predicates.toArray(Predicate[]::new));
        criteria.orderBy(builder.desc(message.get("createdAt")));

        int page = pageRequest.getPageNumber();
        int size = pageRequest.getPageSize();
        int startPosition = page * size;
        TypedQuery<MessageEntity> query = entityManager.createQuery(criteria);
        query.setFirstResult(startPosition);
        query.setMaxResults(size);

        return query.getResultList();
    }

    private List<Predicate> buildPredicates(MessageFilter filter, CriteriaBuilder builder, Root<MessageEntity> message) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.content() != null && !filter.content().isBlank()) {
            predicates.add(builder.like(builder.upper(message.get("content")), "%" + filter.content().toUpperCase() + "%"));
        }

        if (filter.start() != null) {
            predicates.add(builder.greaterThanOrEqualTo(message.get("createdAt"), filter.start()));
        }

        if (filter.end() != null) {
            predicates.add(builder.lessThanOrEqualTo(message.get("createdAt"), filter.end()));
        }

        if (filter.creatorId() != null) {
            predicates.add(builder.equal(message.get("creatorId"), filter.creatorId()));
        }

        if (filter.topicId() != null) {
            predicates.add(builder.equal(message.get("topic").get("topicId"), filter.topicId()));
        }

        if (filter.categoryId() != null) {
            predicates.add(builder.equal(message.get("topic").get("category").get("categoryId"), filter.categoryId()));
        }

        return predicates;
    }
}
