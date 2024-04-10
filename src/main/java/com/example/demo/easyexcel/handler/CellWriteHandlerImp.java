package com.example.demo.easyexcel.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;

public class CellWriteHandlerImp implements CellWriteHandler {
         /**
     * Called after the cell is created
     *
     * @param writeSheetHolder
     * @param writeTableHolder Nullable.It is null without using table writes.
     * @param cell
     * @param head             Nullable.It is null in the case of fill data and without head.
     * @param relativeRowIndex Nullable.It is null in the case of fill data.
     * @param isHead           It will always be false when fill data.
     */
         @Override
         public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
                                 Head head, Integer relativeRowIndex, Boolean isHead) {

         }
}
