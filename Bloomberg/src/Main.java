import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RequestBuilder<IntradayTickData> tickRequest =
			    new IntradayTickRequestBuilder("SPX Index",
			        DateTime.now().minusHours(2),
			        DateTime.now());

			RequestBuilder<IntradayBarData> barRequest =
			    new IntradayBarRequestBuilder("SPX Index",
			        DateTime.now().minusHours(2),
			        DateTime.now())
			        .period(5, TimeUnit.MINUTES);

			RequestBuilder<ReferenceData> refRequest =
			    new ReferenceRequestBuilder("SPX Index", "NAME");

			Future<IntradayTickData> ticks = session.submit(tickRequest);
			Future<IntradayBarData> bars = session.submit(barRequest);
			Future<ReferenceData> name = session.submit(refRequest);
	}

}
