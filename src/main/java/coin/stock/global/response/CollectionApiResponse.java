package coin.stock.global.response;

import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class CollectionApiResponse<T> {

    private final List<T> data;

    private CollectionApiResponse(final List<T> data) {
        this.data = data;
    }

    public static <T> CollectionApiResponse<T> from(final List<T> data) {
        return new CollectionApiResponse<>(data);
    }
}
