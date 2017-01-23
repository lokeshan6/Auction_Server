package shanmuganandam;

import java.util.List;

/**
 * Class provided for ease of test. This will not be used in the project
 * evaluation, so feel free to modify it as you like.
 */
public class Sim {
    private static int case_cnt = 0;
    private static int succ_cnt = 0;
    public static void main(String[] args) {
        Output(TestCase1(), "    //Sellers Can submit new items to the server\n" +
                            "    //Bidders Can request a listing of current items");
        Output(TestCase2(), "    //Sellers will be limited to having maxSellerItems different active items at any given time");
        Output(TestCase3(), "    //Sellers shall be punished for presenting bids over $100.\n" +
                            "    //Sellers will not list items with opening prices exceeding $100\n" +
                            "    //Three bids over $100 should disqualify the seller from bidding\n" +
                            "    //for the rest of the bidding session.");
        Output(TestCase4(), "    //Bidders Can check the price of an item\n" +
                            "    //Bidders Can place a bid on an item\n" +
                            "    //Bidders Can check the outcome of a bid\n" +
                            "    //All prices will be listed in whole dollars only");
        Output(TestCase5(), "    //New bids must at least match the opening bid if no one else has bid yet\n" +
                            "    //OR exceed the current highest bid if other bids have already been placed on the item");
        Output(TestCase6(), "    //Bidders will be limited to having maxBidCount active bids on different current items");
        Output(TestCase7(), "    //Once a Bidder holds the current highest bid for an item\n" +
                            "    //they will only be allowed to successfully place another \n" +
                            "    //bid if another Bidder overtakes them for the current highest bid.\n" +
                            "    //A single Bidder cannot place more than one bid at a single moment");
        Output(TestCase8(), "    //The AuctionServer will have a limit, serverCapacity,\n" +
                            "    //for the number of total items offered from all Sellers");
        Output(TestCase9(), "    //All items will open with non-negative opening prices");
        Output(TestCase10(), "    //If an auction expires with no bids placed, the Seller \n" +
                            "    //will not re-list the item and the server receives no profit from it");
        Output(TestCase11(), "    //Items can receive any number of bids as long as the auction has not expired\n" +
                            "    //Once a bid has been placed it cannot be retracted");
        Output(TestCase12(), "    //Comprehensive Test");
        
        System.out.println("----------");
        System.out.println(String.format("##### Test completed: Success %d test cases out of %d #####",succ_cnt,case_cnt));
    }

    private static void Output(boolean result, String comment) {
        System.out.println("----------");
        System.out.println(String.format("Result: %s\nContent:\n%s", result ? "Pass" : "Fail", comment));
    }
    
    //Sellers Can submit new items to the server
    //Bidders Can request a listing of current items
    private static boolean TestCase1() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        Thread[] sellerThreads = CreateSellers(5, server, 2, 50);
        JoinSellers(sellerThreads);
        List<Item> items = server.getItems();
        if (items.size() == 10) {
            succ_cnt++;
            return true;
        } else {
            return false;
        }
    }

    //Sellers will be limited to having maxSellerItems different active items at any given time
    private static boolean TestCase2() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        Thread[] sellerThreads = CreateSellers(1, server, AuctionServer.maxSellerItems + 1, 50);
        JoinSellers(sellerThreads);
        List<Item> items = server.getItems();
        if (items.size() > AuctionServer.maxSellerItems) {
            return false;
        } else {
            succ_cnt++;
            return true;
        }
    }

    //Sellers shall be punished for presenting bids over $100.
    //Sellers will not list items with opening prices exceeding $100
    //Three bids over $100 should disqualify the seller from bidding
    //for the rest of the bidding session.
    private static boolean TestCase3() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", 99, 200);
        server.submitItem("seller1", "seller1#2", 100, 200);
        server.submitItem("seller1", "seller1#3", 101, 200);
        server.submitItem("seller1", "seller1#4", 101, 200);
        server.submitItem("seller1", "seller1#5", 99, 200);
        server.submitItem("seller1", "seller1#6", 101, 200);
        server.submitItem("seller1", "seller1#7", 99, 200);
        List<Item> items = server.getItems();
        if (items.size() != 3) {
            return false;
        }
        for (Item i : items) {
            if (i.lowestBiddingPrice() > 99) {
                return false;
            }
        }
        succ_cnt++;
        return true;
    }

    //Bidders Can check the price of an item
    //Bidders Can place a bid on an item
    //Bidders Can check the outcome of a bid
    //All prices will be listed in whole dollars only
    private static boolean TestCase4() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", 99, 200);
        server.submitItem("seller1", "seller1#2", 100, 200);
        server.submitItem("seller2", "seller2#1", 101, 200);
        List<Item> items = server.getItems();
        
        if (server.itemPrice(items.get(0).listingID()) != 99) {
            return false;
        }
        if (server.itemPrice(items.get(1).listingID()) != 100) {
            return false;
        }
        int status = server.checkBidStatus("bidder1", items.get(1).listingID());
        if (status != 2) {
            return false;
        }
       
        server.submitBid("bidder1", items.get(1).listingID(), 101);
        
        if (server.itemPrice(items.get(1).listingID()) != 101) {
            return false;
        }
        server.submitBid("bidder2", items.get(1).listingID(), 102);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        status = server.checkBidStatus("bidder1", items.get(1).listingID());
        if (status != 3) {
            return false;
        }
        
        status = server.checkBidStatus("bidder2", items.get(1).listingID());
        if (status != 1) {
            return false;
        }
        succ_cnt++;
        return true;
    }

    //New bids must at least match the opening bid if no one else has bid yet
    //OR exceed the current highest bid if other bids have already been placed on the item
    private static boolean TestCase5() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", 99, 200);
        server.submitItem("seller1", "seller1#2", 100, 200);
        server.submitItem("seller2", "seller2#1", 101, 200);
        List<Item> items = server.getItems();
        if (server.itemPrice(items.get(0).listingID()) != 99) {
            return false;
        }
        if (server.itemPrice(items.get(1).listingID()) != 100) {
            return false;
        }
        if (!server.submitBid("bidder1", items.get(1).listingID(), 100)) {
            return false;
        }
        if (server.itemPrice(items.get(1).listingID()) != 100) {
        	System.out.println("here");
            return false;
        }
        if (!server.submitBid("bidder2", items.get(1).listingID(), 101)) {
            return false;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int status = server.checkBidStatus("bidder1", items.get(1).listingID());
        if (status != 3) {
        	System.out.println("123456 here");
            return false;
        }
        status = server.checkBidStatus("bidder2", items.get(1).listingID());
        if (status != 1) {
        	System.out.println("here");
            return false;
        }
        succ_cnt++;
        return true;
    }

    //Bidders will be limited to having maxBidCount active bids on different current items
    private static boolean TestCase6() {
        case_cnt++;
        AuctionServer server = new AuctionServer();

        for (int i = 0; i < AuctionServer.maxBidCount + 5; i++) {
            server.submitItem("seller1", "seller1#" + i, 99, 200);
        }
        List<Item> items = server.getItems();
        for (Item i : items) {
            server.submitBid("bidder1", i.listingID(), 100);
        }
        try {
            Thread.sleep(200 + 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Item i : items) {
            server.checkBidStatus("bidder1", i.listingID());
        }
        if (server.soldItemsCount() > AuctionServer.maxBidCount) {
            return false;
        } else {
            succ_cnt++;
            return true;
        }
    }

    //Once a Bidder holds the current highest bid for an item
    //they will only be allowed to successfully place another 
    //bid if another Bidder overtakes them for the current highest bid.
    //A single Bidder cannot place more than one bid at a single moment
    private static boolean TestCase7() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", 99, 200);
        List<Item> items = server.getItems();
        if (!server.submitBid("bidder1", items.get(0).listingID(), 100)) {
            return false;
        }
        if (server.submitBid("bidder1", items.get(0).listingID(), 150)) {
            return false;
        }
        if (server.itemPrice(items.get(0).listingID()) != 100) {
            return false;
        }
        if (!server.submitBid("bidder2", items.get(0).listingID(), 101)) {
            return false;
        }
        if (server.itemPrice(items.get(0).listingID()) != 101) {
            return false;
        }
        if (!server.submitBid("bidder1", items.get(0).listingID(), 150)) {
            return false;
        }
        if (server.itemPrice(items.get(0).listingID()) != 150) {
            return false;
        }
        try {
            Thread.sleep(200 + 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int status = server.checkBidStatus("bidder2", items.get(0).listingID());
        if (status != 3) {
            return false;
        }
        status = server.checkBidStatus("bidder1", items.get(0).listingID());
        if (status != 1) {
            return false;
        }
        succ_cnt++;
        return true;
    }

    //The AuctionServer will have a limit, serverCapacity,
    //for the number of total items offered from all Sellers
    private static boolean TestCase8() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        Thread[] sellerThreads = CreateSellers(5, server, 5, 50);
        JoinSellers(sellerThreads);
        List<Item> items = server.getItems();
        if (items.size() > AuctionServer.serverCapacity) {
            return false;
        } else {
            succ_cnt++;
            return true;
        }
    }

    //All items will open with non-negative opening prices
    private static boolean TestCase9() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", -100, 200);
        server.submitItem("seller1", "seller1#2", -1, 200);
        server.submitItem("seller2", "seller2#1", 0, 200);
        server.submitItem("seller2", "seller2#2", 1, 200);
        server.submitItem("seller2", "seller2#3", 100, 200);
        List<Item> items = server.getItems();
        for (Item i : items) {
            if (i.lowestBiddingPrice() < 0) {
                return false;
            }
        }
        succ_cnt++;
        return true;
    }

    //If an auction expires with no bids placed, the Seller 
    //will not re-list the item and the server receives no profit from it
    private static boolean TestCase10() {
        case_cnt++;
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
            return false;
        }
        succ_cnt++;
        return true;
    }

    //Items can receive any number of bids as long as the auction has not expired
    //Once a bid has been placed it cannot be retracted
    private static boolean TestCase11() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        server.submitItem("seller1", "seller1#1", 99, 200);
        List<Item> items = server.getItems();
        server.submitBid("bidder1", items.get(0).listingID(), 99);
        server.submitBid("bidder2", items.get(0).listingID(), 104);
        server.submitBid("bidder1", items.get(0).listingID(), 106);
        // bidder1 want to cancel, but don't have cancel method.
        server.submitBid("bidder1", items.get(0).listingID(), 105);
        try {
            Thread.sleep(200 + 50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.submitBid("bidder2", items.get(0).listingID(), 118);
        int status = server.checkBidStatus("bidder2", items.get(0).listingID());
        if (status != 3) {
            return false;
        }
        status = server.checkBidStatus("bidder1", items.get(0).listingID());
        if (status != 1) {
            return false;
        }
        succ_cnt++;
        return true;
    }
    
    // Comprehensive Test, sum the cashSpent by bidders, then match it with the server's revenue
    private static boolean TestCase12() {
        case_cnt++;
        AuctionServer server = new AuctionServer();
        Bidder[] bidders = new Bidder[20];
        Thread[] sellerThreads = CreateSellers(50, server, 100, 50);
        Thread[] buyerThreads = Createbuyers(20, server, 20, 50, bidders);
        JoinSellers(sellerThreads);
        JoinBuyers(buyerThreads);
        //server.revenueCalculation();
        int cost = 0;
        for(Bidder b:bidders){
            cost += b.cashSpent();
        }
 
        if(server.revenue() != cost){
            return false;
        }
        succ_cnt++;
       
        System.out.println("Total revenue made by the server: " + server.revenue());
        System.out.println("No of items sold: " + server.soldItemsCount());
        return true;
    }
    


    private static Thread[] CreateSellers(int number, AuctionServer server, int cycles, int maxSleepTimeMs) {
        Thread[] sellerThreads = new Thread[number];
        Seller[] sellers = new Seller[number];
        // Start the sellers
        for (int i = 0; i < number; ++i) {
            sellers[i] = new Seller(
                    server,
                    "Seller" + i,
                    cycles, maxSleepTimeMs, i
            );
            sellerThreads[i] = new Thread(sellers[i]);
            sellerThreads[i].start();
        }

        return sellerThreads;
    }

    private static void JoinSellers(Thread[] sellerThreads) {
        // Join on the sellers
        for (int i = 0; i < sellerThreads.length; ++i) {
            try {
                sellerThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static Thread[] Createbuyers(int number, AuctionServer server, int cycles, int maxSleepTimeMs, Bidder[] bidders) {
        Thread[] bidderThreads = new Thread[number];
        // Start the buyers
        for (int i = 0; i < number; ++i) {
            bidders[i] = new Bidder(
                    server,
                    "Buyer" + i,
                    1000, cycles, maxSleepTimeMs, i
            );
            bidderThreads[i] = new Thread(bidders[i]);
            bidderThreads[i].start();
        }

        return bidderThreads;
    }

    private static void JoinBuyers(Thread[] bidderThreads) {
        // Join on the bidders
        for (int i = 0; i < bidderThreads.length; ++i) {
            try {
                bidderThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}