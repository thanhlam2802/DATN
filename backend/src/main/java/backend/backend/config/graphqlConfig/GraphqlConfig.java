package backend.backend.config.graphqlConfig;


import graphql.GraphQLContext;
import graphql.language.StringValue;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Configuration
public class GraphqlConfig {

    @Bean
    public GraphQLScalarType bigDecimalScalar () {
        return ExtendedScalars.GraphQLBigDecimal;
    }
    @Bean
    public GraphQLScalarType TimeintegerScalar () {
        return ExtendedScalars.LocalTime;
    }

    @Bean
    public GraphQLScalarType dateScalar() {
        return ExtendedScalars.Date;
    }

    @Bean
    public GraphQLScalarType dateTimeScalar() {
        return ExtendedScalars.DateTime;
    }

    @Bean
    public GraphQLScalarType instantScalar() {
        return GraphQLScalarType.newScalar()
                .name("Instant") // Tên này PHẢI khớp với tên bạn khai báo trong schema (scalar Instant)
                .description("A Java Instant scalar representing a point in time without a time-zone.")
                .coercing(new Coercing<Instant, String>() {
                    // Phương thức này được gọi khi chuyển đổi Instant từ Java sang String để gửi về client GraphQL
                    @Override
                    public String serialize(Object dataFetcherResult, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingSerializeException {
                        if (dataFetcherResult instanceof Instant) {
                            return ((Instant) dataFetcherResult).toString(); // Chuyển Instant thành chuỗi ISO 8601
                        }
                        throw new CoercingSerializeException("Expected an 'Instant' object but was '" + dataFetcherResult.getClass().getName() + "'.");
                    }

                    // Phương thức này được gọi khi chuyển đổi chuỗi từ biến GraphQL (Input Variables) sang Instant trong Java
                    @Override
                    public Instant parseValue(Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseValueException {
                        try {
                            if (input instanceof String) {
                                return Instant.parse((String) input); // Phân tích chuỗi ISO 8601 thành Instant
                            }
                            throw new CoercingParseValueException("Expected a String for Instant input but was '" + input.getClass().getName() + "'.");
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid Instant format. Expected ISO 8601 string.", e);
                        }
                    }

                    // Phương thức này được gọi khi chuyển đổi chuỗi từ literal trong query (ít dùng với Instant) sang Instant trong Java
                    public Instant parseLiteral(Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue) {
                            try {
                                return Instant.parse(((StringValue) input).getValue());
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Invalid Instant format. Expected ISO 8601 string.", e);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected StringValue for Instant literal.");
                    }
                })
                .build();
    }
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder
                .scalar(dateScalar())
                .scalar(dateTimeScalar())
                .scalar(bigDecimalScalar())
                .scalar(TimeintegerScalar())
                .scalar(instantScalar());
    }



}
