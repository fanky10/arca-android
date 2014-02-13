package com.xtreme.rest.service.test.cases;


import android.test.AndroidTestCase;

import com.xtreme.rest.service.NetworkRequest;
import com.xtreme.rest.service.ProcessingRequest;
import com.xtreme.rest.service.RequestExecutor.ThreadedRequestExecutor;
import com.xtreme.rest.service.test.mock.TestThreadedRequestExecutor;
import com.xtreme.rest.service.test.mock.TestNetworkPrioritizable;
import com.xtreme.rest.service.test.mock.TestNetworkRequest;
import com.xtreme.rest.service.test.mock.TestProcessingPrioritizable;
import com.xtreme.rest.service.test.mock.TestProcessingRequest;
import com.xtreme.rest.service.test.utils.AssertionLatch;

public class RequestExecutorTest extends AndroidTestCase {

	
	public void testRequestExecutorConfig() {
		assertEquals(2, ThreadedRequestExecutor.Config.NUM_NETWORK_THREADS);
		assertEquals(1, ThreadedRequestExecutor.Config.NUM_PROCESSING_THREADS);
		assertEquals(15, ThreadedRequestExecutor.Config.THREAD_KEEP_ALIVE_TIME);
	}
	
	
	// =============================================
	
	
	public void testRequestExecutorInitiallyEmpty() {
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor();
		assertEquals(0, executor.getRequestCount());
		assertTrue(executor.isEmpty());
	}
	
	public void testRequestExecutorEmptyAfterExecutingNetworkRequest() {
		final TestNetworkPrioritizable prioritizable = new TestNetworkPrioritizable();
		final TestNetworkRequest request = new TestNetworkRequest(prioritizable);
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor();
		executor.executeNetworkRequest(request);
		assertEquals(0, executor.getRequestCount());
		assertTrue(executor.isEmpty());
	}
	
	public void testRequestExecutorEmptyAfterExecutingProcessingRequest() {
		final TestProcessingPrioritizable prioritizable = new TestProcessingPrioritizable();
		final TestProcessingRequest request = new TestProcessingRequest(prioritizable);
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor();
		executor.executeProcessingRequest(request);
		assertEquals(0, executor.getRequestCount());
		assertTrue(executor.isEmpty());
	}
	
	
	// =============================================
	
	
	public void testRequestExecutorNetworkRequestCompletes() {
		final RequestHandlerCounter latch = new RequestHandlerCounter(1, 0);
		final TestNetworkPrioritizable prioritizable = new TestNetworkPrioritizable();
		final TestNetworkRequest request = new TestNetworkRequest(prioritizable);
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor() {
			
			@Override
			public void onNetworkRequestComplete(final NetworkRequest<?> r) {
				super.onNetworkRequestComplete(r);
				latch.onNetworkRequestComplete();
				
				assertEquals(request, r);
			}
		};
		executor.executeNetworkRequest(request);
		latch.assertComplete();
	}
	
	public void testRequestExecutorProcessingRequestCompletes() {
		final RequestHandlerCounter latch = new RequestHandlerCounter(0, 1);
		final TestProcessingPrioritizable prioritizable = new TestProcessingPrioritizable();
		final TestProcessingRequest request = new TestProcessingRequest(prioritizable);
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor() {
			
			@Override
			public void onProcessingRequestComplete(final ProcessingRequest<?> r) {
				super.onProcessingRequestComplete(r);
				latch.onProcessingRequestComplete();
				
				assertEquals(request, r);
			}
		};
		executor.executeProcessingRequest(request);
		latch.assertComplete();
	}
	
	
	// =============================================
	
	
	public void testRequestExecutorNetworkRequestCancelled() {
		final RequestHandlerCounter latch = new RequestHandlerCounter(1, 0);
		final TestNetworkPrioritizable prioritizable = new TestNetworkPrioritizable();
		final TestNetworkRequest request = new TestNetworkRequest(prioritizable);
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor() {
			
			@Override
			public void onNetworkRequestCancelled(final NetworkRequest<?> r) {
				super.onNetworkRequestCancelled(r);
				latch.onNetworkRequestCancelled();
				
				assertEquals(request, r);
			}
		};
		request.cancel();
		executor.executeNetworkRequest(request);
		latch.assertComplete();
	}
	
	public void testRequestExecutorProcessingRequestCancelled() {
		final RequestHandlerCounter latch = new RequestHandlerCounter(0, 1);
		final TestProcessingPrioritizable prioritizable = new TestProcessingPrioritizable();
		final TestProcessingRequest request = new TestProcessingRequest(prioritizable);
		final TestThreadedRequestExecutor executor = new TestThreadedRequestExecutor() {
			
			@Override
			public void onProcessingRequestCancelled(final ProcessingRequest<?> r) {
				super.onProcessingRequestCancelled(r);
				latch.onProcessingRequestCancelled();
				
				assertEquals(request, r);
			}
		};
		request.cancel();
		executor.executeProcessingRequest(request);
		latch.assertComplete();
	}
	
	
	// =============================================
	
	
	private static class RequestHandlerCounter {

		private final AssertionLatch mNetworkLatch;
		private final AssertionLatch mProcessingLatch;
		
		public RequestHandlerCounter(final int networkCount, final int processingCount) {
			mNetworkLatch = new AssertionLatch(networkCount);
			mProcessingLatch = new AssertionLatch(processingCount);
		}
		
		public void onNetworkRequestComplete() {
			mNetworkLatch.countDown();
		}
		
		public void onNetworkRequestCancelled() {
			mNetworkLatch.countDown();
		}
		
		public void onProcessingRequestComplete() {
			mProcessingLatch.countDown();
		}
		
		public void onProcessingRequestCancelled() {
			mProcessingLatch.countDown();
		}
		
		public void assertComplete() {
			mNetworkLatch.assertComplete();
			mProcessingLatch.assertComplete();
		}
	}
}
