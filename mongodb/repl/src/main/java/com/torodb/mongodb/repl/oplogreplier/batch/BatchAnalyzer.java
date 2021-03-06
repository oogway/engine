/*
 * ToroDB
 * Copyright © 2014 8Kdata Technology (www.8kdata.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.torodb.mongodb.repl.oplogreplier.batch;

import static com.torodb.mongowp.commands.oplog.OplogOperationType.DB;
import static com.torodb.mongowp.commands.oplog.OplogOperationType.DB_CMD;
import static com.torodb.mongowp.commands.oplog.OplogOperationType.INSERT;
import static com.torodb.mongowp.commands.oplog.OplogOperationType.UPDATE;

import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;
import com.torodb.core.logging.LoggerFactory;
import com.torodb.mongodb.repl.oplogreplier.ApplierContext;
import com.torodb.mongodb.repl.oplogreplier.analyzed.AnalyzedOpReducer;
import com.torodb.mongodb.utils.NamespaceUtil;
import com.torodb.mongowp.commands.oplog.CollectionOplogOperation;
import com.torodb.mongowp.commands.oplog.DbCmdOplogOperation;
import com.torodb.mongowp.commands.oplog.OplogOperation;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

public class BatchAnalyzer implements Function<List<OplogOperation>, List<AnalyzedOplogBatch>> {

  private final Logger logger;
  private final ApplierContext context;
  private final AnalyzedOpReducer analyzedOpReducer;
  private static final ImmutableSet<String> SYSTEM_COLLECTIONS = ImmutableSet.<String>builder()
      .add(NamespaceUtil.NAMESPACES_COLLECTION)
      .add(NamespaceUtil.INDEXES_COLLECTION)
      .add(NamespaceUtil.PROFILE_COLLECTION)
      .add(NamespaceUtil.JS_COLLECTION)
      .build();

  @Inject
  public BatchAnalyzer(@Assisted ApplierContext context, LoggerFactory lf,
      AnalyzedOpReducer analyzedOpReducer) {
    this.logger = lf.apply(this.getClass());
    this.context = context;
    this.analyzedOpReducer = analyzedOpReducer;
  }

  @Override
  public List<AnalyzedOplogBatch> apply(List<OplogOperation> oplogOps) {
    List<AnalyzedOplogBatch> result = new ArrayList<>();

    int fromExcluded = -1;

    for (int i = 0; i < oplogOps.size(); i++) {
      OplogOperation op = oplogOps.get(i);
      switch (op.getType()) {
        case DB:
        case NOOP:
          logger.debug("Ignoring operation {}", op);
          break;
        case DB_CMD:
          addParallelToBatch(oplogOps, fromExcluded, i, result);
          fromExcluded = i;
          result.add(new SingleOpAnalyzedOplogBatch((DbCmdOplogOperation) op));
          break;
        case DELETE:
        case INSERT:
        case UPDATE: {
          //CUD operations on system collection must be addressed sequentially
          if (SYSTEM_COLLECTIONS.contains(((CollectionOplogOperation) op).getCollection())) {
            addParallelToBatch(oplogOps, fromExcluded, i, result);
            fromExcluded = i;
            result.add(new SingleOpAnalyzedOplogBatch(op));
          }
          break;
        }
        default:
          throw new AssertionError("Found an unknown oplog operation " + op);
      }
    }
    addParallelToBatch(oplogOps, fromExcluded, oplogOps.size(), result);

    return result;
  }

  private void addParallelToBatch(List<OplogOperation> allOperations, int fromExcluded,
      int toExcluded, List<AnalyzedOplogBatch> batches) {
    int from = fromExcluded + 1;
    if (from == toExcluded) {
      return;
    }
    batches.add(new CudAnalyzedOplogBatch(allOperations.subList(from, toExcluded), context,
        analyzedOpReducer));
  }

  public static interface BatchAnalyzerFactory {

    public BatchAnalyzer createBatchAnalyzer(ApplierContext context);
  }
}
