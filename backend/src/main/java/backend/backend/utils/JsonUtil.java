package backend.backend.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Cấu hình ObjectMapper để có thể xử lý tốt hơn
        // Bật tính năng làm đẹp JSON khi serialize (indentation)
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Tắt tính năng thất bại khi gặp thuộc tính không xác định trong JSON
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Tắt tính năng ghi ngày tháng dưới dạng timestamp
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Có thể thêm các cấu hình khác tùy theo nhu cầu
    }

    /**
     * Chuyển đổi một đối tượng Java thành chuỗi JSON.
     *
     * @param object Đối tượng Java cần chuyển đổi.
     * @return Chuỗi JSON biểu diễn đối tượng.
     * @throws JsonProcessingException nếu có lỗi trong quá trình xử lý JSON.
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Chuyển đổi một đối tượng Java thành chuỗi JSON với định dạng đẹp (pretty print).
     * (Thường đã được bật trong static block, nhưng phương thức này đảm bảo rõ ràng hơn)
     *
     * @param object Đối tượng Java cần chuyển đổi.
     * @return Chuỗi JSON được định dạng đẹp.
     * @throws JsonProcessingException nếu có lỗi trong quá trình xử lý JSON.
     */
    public static String toPrettyJson(Object object) throws JsonProcessingException {
        // Tạo một ObjectMapper mới hoặc cấu hình tạm thời để đảm bảo pretty print
        // (Trong trường hợp static objectMapper đã bị disable INDENT_OUTPUT ở đâu đó)
        ObjectMapper prettyPrinter = new ObjectMapper();
        prettyPrinter.enable(SerializationFeature.INDENT_OUTPUT);
        return prettyPrinter.writeValueAsString(object);
    }

    /**
     * Chuyển đổi chuỗi JSON thành một đối tượng Java của một Class cụ thể.
     *
     * @param json Chuỗi JSON.
     * @param clazz Class của đối tượng đích.
     * @param <T> Kiểu của đối tượng đích.
     * @return Đối tượng Java đã được chuyển đổi.
     * @throws JsonProcessingException nếu có lỗi trong quá trình xử lý JSON.
     * @throws IOException nếu có lỗi I/O.
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException, IOException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Chuyển đổi chuỗi JSON thành một List các đối tượng Java.
     * Hữu ích khi JSON đại diện cho một mảng các đối tượng.
     *
     * @param json Chuỗi JSON biểu diễn một mảng.
     * @param elementClazz Class của các phần tử trong List.
     * @param <T> Kiểu của các phần tử trong List.
     * @return List các đối tượng Java.
     * @throws JsonProcessingException nếu có lỗi trong quá trình xử lý JSON.
     * @throws IOException nếu có lỗi I/O.
     */
    public static <T> List<T> fromJsonList(String json, Class<T> elementClazz) throws JsonProcessingException, IOException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, elementClazz));
    }

    /**
     * Chuyển đổi chuỗi JSON thành một Map.
     * Hữu ích khi không biết cấu trúc chính xác của JSON hoặc muốn thao tác với các trường động.
     *
     * @param json Chuỗi JSON.
     * @return Map biểu diễn dữ liệu JSON.
     * @throws JsonProcessingException nếu có lỗi trong quá trình xử lý JSON.
     * @throws IOException nếu có lỗi I/O.
     */
    public static Map<String, Object> fromJsonToMap(String json) throws JsonProcessingException, IOException {
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Lấy giá trị của một trường cụ thể từ chuỗi JSON mà không cần deserialize toàn bộ.
     * (Thường dùng cho các trường hợp đặc biệt hoặc JSON lớn)
     *
     * @param json Chuỗi JSON.
     * @param fieldName Tên trường muốn lấy giá trị.
     * @return Giá trị của trường dưới dạng String, hoặc null nếu không tìm thấy.
     * @throws JsonProcessingException nếu có lỗi trong quá trình xử lý JSON.
     * @throws IOException nếu có lỗi I/O.
     */
    public static String getFieldValue(String json, String fieldName) throws JsonProcessingException, IOException {
        // Read the JSON as a tree
        return objectMapper.readTree(json).path(fieldName).asText();
    }
}