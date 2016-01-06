/* 
 * Copyright (C) 2015 Pivotal Software, Inc.
 *
 * Licensed under the Modified BSD License.
 *
 * All rights reserved.
 */
package io.pivotal.arca.dispatcher;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import io.pivotal.arca.dispatcher.RequestDispatcher.AbstractRequestDispatcher;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ModernRequestDispatcher extends AbstractRequestDispatcher {

	private final Context mContext;
	private final LoaderManager mLoaderManager;

	public ModernRequestDispatcher(final RequestExecutor executor, final Context context, final LoaderManager manager) {
		super(executor);
		mContext = context;
		mLoaderManager = manager;
	}

	@Override
	public void execute(final Query request, final QueryListener listener) {
		final LoaderCallbacks<?> callbacks = new QueryLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	@Override
	public void execute(final Update request, final UpdateListener listener) {
		final LoaderCallbacks<?> callbacks = new UpdateLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	@Override
	public void execute(final Insert request, final InsertListener listener) {
		final LoaderCallbacks<?> callbacks = new InsertLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	@Override
	public void execute(final Delete request, final DeleteListener listener) {
		final LoaderCallbacks<?> callbacks = new DeleteLoaderCallbacks(listener);
		execute(request, callbacks);
	}

	private void execute(final Request<?> request, final LoaderCallbacks<?> callbacks) {
		final int identifier = request.getIdentifier();
		final Bundle bundle = createRequestBundle(request);
		mLoaderManager.restartLoader(identifier, bundle, callbacks);
	}

	// ========================================

	private class QueryLoaderCallbacks implements LoaderCallbacks<QueryResult> {

		private final RequestListener<QueryResult> mListener;

		public QueryLoaderCallbacks(final QueryListener listener) {
			mListener= listener;
		}

		@Override
		public Loader<QueryResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Query request = args.getParcelable(Extras.REQUEST);
			return new ModernQueryLoader(mContext, executor, request);
		}

		@Override
		public void onLoadFinished(final Loader<QueryResult> loader, final QueryResult result) {
			if (mListener != null) {
				mListener.onRequestComplete(result);
			}
		}

		@Override
		public void onLoaderReset(final Loader<QueryResult> loader) {
			if (mListener != null) {
				mListener.onRequestComplete(new QueryResult((Cursor) null));
			}
		}
	}

	private class UpdateLoaderCallbacks implements LoaderCallbacks<UpdateResult> {

		private final RequestListener<UpdateResult> mListener;

		public UpdateLoaderCallbacks(final UpdateListener listener) {
			mListener= listener;
		}

		@Override
		public Loader<UpdateResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Update request = args.getParcelable(Extras.REQUEST);
			return new ModernUpdateLoader(mContext, executor, request);
		}

		@Override
		public void onLoadFinished(final Loader<UpdateResult> loader, final UpdateResult result) {
			if (mListener != null) {
				mListener.onRequestComplete(result);
			}
		}

		@Override
		public void onLoaderReset(final Loader<UpdateResult> loader) {
			if (mListener != null) {
				mListener.onRequestComplete(new UpdateResult(0));
			}
		}
	}

	private class InsertLoaderCallbacks implements LoaderCallbacks<InsertResult> {

		private final RequestListener<InsertResult> mListener;

		public InsertLoaderCallbacks(final InsertListener listener) {
			mListener= listener;
		}

		@Override
		public Loader<InsertResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Insert request = args.getParcelable(Extras.REQUEST);
			return new ModernInsertLoader(mContext, executor, request);
		}

		@Override
		public void onLoadFinished(final Loader<InsertResult> loader, final InsertResult result) {
			if (mListener != null) {
				mListener.onRequestComplete(result);
			}
		}

		@Override
		public void onLoaderReset(final Loader<InsertResult> loader) {
			if (mListener != null) {
				mListener.onRequestComplete(new InsertResult(0));
			}
		}
	}

	private class DeleteLoaderCallbacks implements LoaderCallbacks<DeleteResult> {

		private final RequestListener<DeleteResult> mListener;

		public DeleteLoaderCallbacks(final DeleteListener listener) {
			mListener= listener;
		}

		@Override
		public Loader<DeleteResult> onCreateLoader(final int id, final Bundle args) {
			final RequestExecutor executor = getRequestExecutor();
			final Delete request = args.getParcelable(Extras.REQUEST);
			return new ModernDeleteLoader(mContext, executor, request);
		}

		@Override
		public void onLoadFinished(final Loader<DeleteResult> loader, final DeleteResult result) {
			if (mListener != null) {
				mListener.onRequestComplete(result);
			}
		}

		@Override
		public void onLoaderReset(final Loader<DeleteResult> loader) {
			if (mListener != null) {
				mListener.onRequestComplete(new DeleteResult(0));
			}
		}
	}
}
