package runtime;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import exceptions.CouldNotGetObjectException;

public class Main {
	
	public static void main(String[] args){
		Document doc = null;
		String day;
		String month;
		String year;
		String meet;
		String time;
		String best;
		if (args.length < 6){
			System.out.println("Should supply 6 args, day, month, year, race meeting, time and Y/N");
			return;
		}
		day = args[0];
		if (!day.matches("[0-3][0-9]")){
			System.out.println("First arg is 'day' - should be 2 digits");
			return;
		}
		month = args[1];
		if (!month.matches("[0-1][0-9]")){
			System.out.println("2nd arg is 'month' - should be 2 digits");
			return;
		}
		year = args[2];
		if (!year.matches("20[0-1][0-9]")){
			System.out.println("3rd arg is 'year' - should be 4 digits");
			return;
		}
		meet = args[3];
		if (!meet.matches("[a-z]*")){
			System.out.println("4th arg is the race meeting as shown on Oddschecker");
			return;
		}
		time = args[4];
		if (!time.matches("[0-2][0-9]:[0-9][0-9]")){
			System.out.println("5th arg is a HH:mm string");
			return;
		}
		best = args[5];
		if (!best.matches("Y|N")){
			System.out.println("6th arg is Y or N for 'display the best odds only'");
			return;
		}
		
		String page = "http://www.oddschecker.com/horse-racing/" + year + "-" + month + "-" + day + "-" + 
				meet + "/" + time + "/winner";
		System.out.println("Best prices for the " + year + "-" + month + "-" + day + " " + meet + " " + time);
		try {
			doc = goToPage(page);
		}
		catch (CouldNotGetObjectException e) {
			e.printStackTrace();
			return;
		}

		ArrayList<String> bookies = new ArrayList<String>();
		for (Element e : doc.select("tr.eventTableHeader td[data-bk]")){
			bookies.add(e.select("a").attr("title"));
		}

		ArrayList<BookieOdds> bookieOdds = new ArrayList<BookieOdds>(bookies.size());
		int i = 1;
		for (Element e : doc.select("tr.diff-row")){
			i = 1;
			System.out.println(e.attr("data-bname"));
			for (Element f : e.select("td.o")){
				bookieOdds.add(new BookieOdds(bookies.get(i), Float.parseFloat(f.attr("data-odig"))));
				if (best.equals("N")){
					System.out.println(bookies.get(i) + " - " + f.attr("data-o"));
				}
				i++;
			}
			if (best.equals("Y")){
				String bestBookie = "";
				float bestOdds = 0;
				for (BookieOdds odds : bookieOdds){
					if (odds.getOdds() == bestOdds){
						bestBookie += "|" + odds.getName();
					}
					if (odds.getOdds() > bestOdds){
						bestOdds = odds.getOdds();
						bestBookie = odds.getName();
					}
				}
				System.out.println("Best oods: " + bestBookie + " - " + bestOdds);
			}
			System.out.println("");
		}
	}
	

	public static org.jsoup.nodes.Document goToPage(String page) throws CouldNotGetObjectException{
		int tries = 0;
		while (tries < 4){
			try {
				return Jsoup.connect(page).timeout(16000).userAgent("Mozilla").get();
			}
			catch (SocketTimeoutException e) {
			}
			catch (IOException e) {
			}
			tries++;
		}
		throw new CouldNotGetObjectException(page);
	}

}

class BookieOdds{
	
	private String name;
	private float odds;
	
	public BookieOdds(String name, float odds) {
		this.name = name;
		this.odds = odds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getOdds() {
		return odds;
	}

	public void setOdds(float odds) {
		this.odds = odds;
	}
	
}
