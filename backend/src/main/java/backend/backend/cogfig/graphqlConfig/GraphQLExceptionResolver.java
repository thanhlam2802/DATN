package backend.backend.cogfig.graphqlConfig;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter; // Quay lại adapter
import org.springframework.graphql.execution.ErrorType; // Đảm bảo import đúng ErrorType
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Global exception handler for GraphQL operations.
 * Maps specific Java exceptions to standardized GraphQL error formats.
 */
@Component
public class GraphQLExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable ex, DataFetchingEnvironment env) {
        GraphQLError error;

        if (ex instanceof IllegalArgumentException) {
            error = buildError(env, ex.getMessage(), ErrorType.BAD_REQUEST);
        } else if (ex instanceof EntityNotFoundException) {
            error = buildError(env, "Không tìm thấy dữ liệu", ErrorType.NOT_FOUND);
        } else if (ex instanceof DataIntegrityViolationException
                || ex.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
            error = buildError(env, "Dữ liệu bị trùng hoặc vi phạm ràng buộc", ErrorType.BAD_REQUEST);
        } else {
            error = buildError(env, "Lỗi máy chủ không xác định", ErrorType.INTERNAL_ERROR);
        }

        return Mono.just(List.of(error));
    }

    private GraphQLError buildError(DataFetchingEnvironment env, String message, ErrorType type) {
        return GraphqlErrorBuilder.newError(env)
                .message(message)
                .errorType(type)
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .build();
    }
}
