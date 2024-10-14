package kr.post.absent;

import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("page")
@Data
@Lazy
public class Pagination {

    private int totalCount, startRow, endRow, rowNum,
            pageCount, pageSize, startPage, endPage, pageNum,
            blockCount, prevBlock, nextBlock, blockNum;
    /*
     request값으로 넘어올 경우 필요한 것 : rowNum(상세페이지), pageNum(), blockNum(이전 블락 이후 블락 필요함 -> >> 이거! )
     row (count start end)
     ->
     page (count size start end)
     ->
     block (count size prev next)
     */

    private String tableName;
    private boolean existPrev, existNext;

    public Pagination() {}

    private final int BLOCK_SIZE = 5;

    public final int PAGE_SIZE = 10;




    public Pagination(int pageNum, int count, int pageSize) {
        this.pageNum = pageNum;
        this.totalCount = count;
        this.pageSize = pageSize;

        this.pageCount = (totalCount % pageSize == 0) ? totalCount / pageSize : totalCount / pageSize + 1;
        this.blockCount = (pageCount % BLOCK_SIZE == 0) ? pageCount / BLOCK_SIZE : pageCount / BLOCK_SIZE + 1;
        this.startRow = (pageNum - 1) * pageSize;
        this.endRow = pageNum * pageSize - 1;
        this.blockNum = (pageNum - 1) * BLOCK_SIZE;
        this.startPage = (pageNum <= 3) ? 1 : pageNum - 2;
        this.endPage = (endPage == blockCount) ? blockCount : pageNum + 2;
        this.existPrev = (pageNum > 1);
        this.existNext = (pageNum < pageCount);
        this.nextBlock = startPage + BLOCK_SIZE;
        this.prevBlock = startPage - BLOCK_SIZE;

    }

    public Pagination(int pageNum, int totalCount) {
        this.pageNum = pageNum;
        this.totalCount = totalCount;
        this.pageCount = (totalCount % PAGE_SIZE == 0) ? totalCount / PAGE_SIZE : totalCount / PAGE_SIZE + 1;
        this.blockCount = (pageCount + BLOCK_SIZE - 1) / BLOCK_SIZE;
        this.startRow = (pageNum - 1) * PAGE_SIZE;
        this.endRow = (pageNum * PAGE_SIZE) - 1;
        this.blockNum = (pageNum - 1) * BLOCK_SIZE;
        this.startPage = (pageNum <= 3) ? 1 : pageNum - 2;
        this.endPage = (endPage >= pageCount - 2) ? pageCount : pageNum + 2;
        this.existPrev = (pageNum <= 1) ? false : true;
        this.existNext = (pageNum == pageCount) ? false : true;

    }
}
