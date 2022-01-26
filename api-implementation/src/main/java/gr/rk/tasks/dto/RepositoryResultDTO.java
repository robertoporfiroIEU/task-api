package gr.rk.tasks.dto;

import javax.persistence.TypedQuery;

public class RepositoryResultDTO<T> {
    private TypedQuery<Long> totalResultTypedQuery;
    private TypedQuery<T> resultTypedQuery;

    public RepositoryResultDTO(TypedQuery<Long> totalResultTypedQuery, TypedQuery<T> resultTypedQuery) {
        this.totalResultTypedQuery = totalResultTypedQuery;
        this.resultTypedQuery = resultTypedQuery;
    }

    public TypedQuery<Long> getTotalResultTypedQuery() {
        return totalResultTypedQuery;
    }

    public void setTotalResultTypedQuery(TypedQuery<Long> totalResultTypedQuery) {
        this.totalResultTypedQuery = totalResultTypedQuery;
    }

    public TypedQuery<T> getResultTypedQuery() {
        return resultTypedQuery;
    }

    public void setResultTypedQuery(TypedQuery<T> resultTypedQuery) {
        this.resultTypedQuery = resultTypedQuery;
    }
}
