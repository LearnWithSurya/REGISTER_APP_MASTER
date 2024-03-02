package in.surya.binding;

import java.util.List;

import lombok.Data;

@Data
public class PageableResponse<T> {
	private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
