package shanmuganandam;

import java.util.List;

import shanmuganandam.AuctionServer;

/**
 * Class provided for ease of test. This will not be used in the project
 * evaluation, so feel free to modify it as you like.
 */
public class Simulation {
	public static void main(String[] args) {
		int nrSellers = 50;
		int nrBidders = 20;

		Thread[] sellerThreads = new Thread[nrSellers];
		Thread[] bidderThreads = new Thread[nrBidders];
		Seller[] sellers = new Seller[nrSellers];
		Bidder[] bidders = new Bidder[nrBidders];

		// Start the sellers
		for (int i = 0; i < nrSellers; ++i) {
			sellers[i] = new Seller(AuctionServer.getInstance(), "Seller" + i, 100, 50, i);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		// Start the buyers
		for (int i = 0; i < nrBidders; ++i) {
			bidders[i] = new Bidder(AuctionServer.getInstance(), "Buyer" + i, 1000, 20, 150, i);
			bidderThreads[i] = new Thread(bidders[i]);
			bidderThreads[i].start();
		}

		// Join on the sellers
		for (int i = 0; i < nrSellers; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Join on the bidders
		for (int i = 0; i < nrBidders; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// TODO: Add code as needed to debug
		// Pseudo-code:
		// Create an instance of the AuctionServer class by calling the
		// getInstance() method of that class
		// FOR each traverse the highestBids HashMap to get the corresponding
		// listingID
		// Get the item for the listingID from the itemsAndIDs HashMap
		// Get the highestBid and calculate the profit for item = highestBid -
		// openingBid
		// END LOOP
		// Calculate total revenue.

		AuctionServer server = AuctionServer.getInstance();
		server.revenue();
		server.soldItemsCount();
		testCase1();
		testCase2();
		testCase3();
		testCase4();
	}

	/**
	 * Test case to check if the seller can submit items opening price exceeding 99$ three times
	 */
	private static boolean testCase1() {
		System.out.println("Test Case 1: Check if the seller can submit items opening price exceeding 99$ three times");
		AuctionServer server = new AuctionServer();
		server.submitItem("seller1", "seller1#1", 102, 200);
		server.submitItem("seller1", "seller1#2", 100, 200);
		server.submitItem("seller1", "seller1#3", 101, 200);
		server.submitItem("seller1", "seller1#4", 101, 200);
		server.submitItem("seller1", "seller1#5", 103, 200);
		List<Item> items = server.getItems();
		if (items.size() > 3) {
			System.out.println("Test Case 1 has failed");
			return false;
		}
		System.out.println("Test Case 1 has passed");
		return true;
	}
	
	/**
	 * Test case to Check if all items will have non negative opening prices
	 */
	private static boolean testCase2() {
		System.out.println("Test Case 2: Check if all items will have non negative opening prices");
		AuctionServer server = new AuctionServer();
		server.submitItem("seller1", "seller1#1", -1, 200);
		server.submitItem("seller1", "seller1#2", -90, 200);
		server.submitItem("seller1", "seller1#3", 102, 200);
		List<Item> items = server.getItems();
		for (Item i : items) {
			if (i.lowestBiddingPrice() < 0) {
				System.out.println("Test Case 2 has failed");
				return false;
			}
		}
		System.out.println("Test Case 2 has passed");
		return true;
	}
	
	/**
	 * Test case to Check if the seller is disqualified if five or more of its items expire before anybody can bid
	 */
	private static boolean testCase3() {
		System.out.println("Test Case3: Check if the seller is disqualified if five or more of its items expire before anybody can bid");
		AuctionServer server = new AuctionServer();
		server.submitItem("seller1", "seller1#1", 17, 0);
		server.submitItem("seller1", "seller1#2", 89, 0);
		server.submitItem("seller1", "seller1#3", 90, 0);
		server.submitItem("seller1", "seller1#4", 18, 0);
		server.submitItem("seller1", "seller1#5", 90, 0);
		server.submitItem("seller1", "seller1#6", 102, 200);
		server.submitItem("seller1", "seller1#7", 76, 200);
		List<Item> items = server.getItems();
		if (items.size() > 0) {
			System.out.println("Test Case 3 has failed");
			return false;
		}
		System.out.println("Test Case 3 has passed");
		return true;
	}
	
	/**
	 * Test case to Check if the seller cannot re-list the item, if the auction expires and no bids have been placed the first time
	 */
	
    private static boolean testCase4() {
    	System.out.println("Test Case4: Check if the seller cannot re-list the item, if the auction expires and no bids "
    			+ "have been placed the first time"); 
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", 99, 200);
        List<Item> items = server.getItems();
        if (items.size() != 1) {
            return false;
        }
        try {
            Thread.sleep(200 + 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.checkBidStatus("bidder1", items.get(0).listingID());
        server.submitItem("seller1", "seller1#1", 98, 200);
        items = server.getItems();
        if (items.size() != 0) {
        	System.out.println("Test Case 4 has failed");
            return false;
        }
        System.out.println("Test Case 4 has passed");
        return true;
    }
}