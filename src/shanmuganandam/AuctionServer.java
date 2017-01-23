package shanmuganandam;

import java.awt.event.ItemEvent;

/**
 * @author Lokesh Shanmuganandam
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuctionServer {

    /**
     * Singleton: the following code makes the server a Singleton. You should
     * not edit the code in the following noted section.
     *
     * For test purposes, we made the constructor protected.
     */

    /* Singleton: Begin code that you SHOULD NOT CHANGE! */
    protected AuctionServer() {
    }

    private static AuctionServer instance = new AuctionServer();

    public static AuctionServer getInstance() {
        return instance;
    }

    /* Singleton: End code that you SHOULD NOT CHANGE! */
    /* Statistic variables and server constants: Begin code you should likely leave alone. */
    /**
     * Server statistic variables and access methods:
     */
    private int soldItemsCount = 0;
    private int revenue = 0;

    public synchronized int soldItemsCount() {
        //revenueCalculation();
        for (int listingID : highestBids.keySet()) {
            Item item = itemsAndIDs.get(listingID);
            int highestBid = highestBids.get(item.listingID());
            int profit = highestBid - item.lowestBiddingPrice();
            if (profit > 0) {
                soldItemsCount++;
            }

        }
        System.out.println("Sold Items Count: "+this.soldItemsCount);
        return this.soldItemsCount;
    }

    public synchronized int revenue() {
        //revenueCalculation();
        for (int listingID : highestBids.keySet()) {
            Item item = itemsAndIDs.get(listingID);
            int highestBid = highestBids.get(item.listingID());
            int profit = highestBid - item.lowestBiddingPrice();
            if (profit > 0) {
                this.revenue += highestBid;
            }

        }
        System.out.println("Revenue: "+this.revenue);
        return this.revenue;
    }

    /**
     * Server restriction constants:
     */
    public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
    public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
    public static final int serverCapacity = 80; // The maximum number of active items at a given time.

    /* Statistic variables and server constants: End code you should likely leave alone. */
    /**
     * Some variables we think will be of potential use as you implement the
     * server...
     */
    // List of items currently up for bidding (will eventually remove things that have expired).
    private List<Item> itemsUpForBidding = new ArrayList<Item>();

    // The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
    private int lastListingID = -1;

    // List of item IDs and actual items.  This is a running list with everything ever added to the auction.
    private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

    // List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
    private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

    // List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
    private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>();

    // List of sellers and how many items they have currently up for bidding.
    private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

    // List of buyers and how many items on which they are currently bidding.
    private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();

    // List of sellers and how many items that have opening price greater than 99$
    private HashMap<String, Integer> openingPriceCheck = new HashMap<String, Integer>();

    //List of sellers and count of items that expires before anyone can bid
    private HashMap<String, Integer> biddingDurationCheck = new HashMap<String, Integer>();

    private List<String> disqualifiedSellers = new ArrayList<String>();

    /**
     * Invariants : in all instances the count of itemsUpForBidding <=
     * serverCapacity in all instances the count of itemsPerSeller for the given
     * seller name <= maxSellerItems in all instances the count of itemsPerBuyer
     * for the given bidder name <= maxBidCount
     */
    // Object used for instance synchronization if you need to do it at some point 
    // since as a good practice we don't use synchronized (this) if we are doing internal
    // synchronization.
    //
    // private Object instanceLock = new Object(); 
    /*
     *  The code from this point forward can and should be changed to correctly and safely 
     *  implement the methods as needed to create a working multi-threaded server for the 
     *  system.  If you need to add Object instances here to use for locking, place a comment
     *  with them saying what they represent.  Note that if they just represent one structure
     *  then you should probably be using that structure's intrinsic lock.
     */
    /**
     * Attempt to submit an <code>Item</code> to the auction
     *
     * @param sellerName Name of the <code>Seller</code>
     * @param itemName Name of the <code>Item</code>
     * @param lowestBiddingPrice Opening price
     * @param biddingDurationMs Bidding duration in milliseconds
     * @return A positive, unique listing ID if the <code>Item</code> listed
     * successfully, otherwise -1
     */
    /**
     * @Precondition: sellerName, itemName, lowestBiddingPrice,
     * biddingDurationMs != null lowestBiddingPrice > 0, biddingDurationMs > 0
     * @Postcondition: return unique positive listing ID if the item can be
     * successfully placed for bid or return -1 if the item cannot be placed for
     * bid.
     * @Exception: none
     *
     */
    public synchronized int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs) {
		// TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //   Make sure there's room in the auction site.
        //   If the seller is a new one, add them to the list of sellers.
        //   If the seller has too many items up for bidding, don't let them add this one.
        //   Don't forget to increment the number of things the seller has currently listed.

        //	Pseudo-code: submitItem()
        //	Synchronized(this)
        //	IF count of itemsUpForBidding > serverCapacity
        //		return -1;
        //	END IF
        //	IF the itemsPerSeller HashMap does not contains the given sellerName THEN
        //		Add an entry for the sellerName in the itemsPerSeller HashMap with count 1
        //		Create a new entry for the item with the given itemName with a unique listing ID in itemsAndIDs HashMap
        //		Add the item to the itemsUpForBidding list
        //		Create a new entry for the seller name with a count of 1 in the itemsPerSeller HashMap
        //		Add an entry for the listingID and the lowestBiddingPrice in the highestBids HashMap
        //		return listingID;
        //	ELSE
        //		Get the item count from the itemsPerSeller HashMap for the given sellerName
        //		IF the item count >  maxSellerItems
        //			return -1;
        //		ELSE
        //			update the entry for the items count in the itemsPerSeller HashMap for the given sellerName 	
        //			Create a new entry for the item with the given itemName with a unique listing ID in itemsAndIDs HashMap 
        //			Add the item to the itemsUpForBidding list
        //			Update the seller items count in the itemsPerSeller HashMap
        //			Add an entry for the listingID and the lowestBiddingPrice in the highestBids HashMap
        //			return listingID;
        //		END IF
        //	END IF
        for (Item i : itemsUpForBidding) {
            if (i.name().equalsIgnoreCase(itemName) && i.seller().equalsIgnoreCase(sellerName)) {
                if (!highestBidders.containsKey(i.listingID())) {
                    return -1;
                }
            }
        }
        if (itemsUpForBidding.size() > serverCapacity) {
            //System.out.println("The server has reached its maximum capacity");
            return -1;
        }
        if (lowestBiddingPrice > 0) {
            if (biddingDurationMs > 0) {
                if (!(disqualifiedSellers.contains(sellerName))) {
                    if (!(itemsPerSeller.containsKey(sellerName))) {
                        if (lowestBiddingPrice > 99) {
                            openingPriceCheck.put(sellerName, 1);
                        }
                        itemsPerSeller.put(sellerName, 1);
                        lastListingID++;
                        Item i = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
                        itemsAndIDs.put(lastListingID, i);
                        itemsUpForBidding.add(i);
                        itemsPerSeller.put(sellerName, 1);
                        highestBids.put(lastListingID, lowestBiddingPrice);
                        return lastListingID;
                    } else {
                        if (lowestBiddingPrice > 99) {
                            if (openingPriceCheck.containsKey(sellerName)) {
                                int openingPriceCheckCount = openingPriceCheck.get(sellerName) + 1;
                                openingPriceCheck.put(sellerName, openingPriceCheckCount);
                                if (openingPriceCheck.get(sellerName) >= 3) {
                                    System.out.println("The seller: " + sellerName + " is disqualified for submitting items with opening price > $99 three times");
                                    disqualifiedSellers.add(sellerName);
                                    return -1;
                                }

                            } else {
                                openingPriceCheck.put(sellerName, 1);
                            }

                        }

                        int sellerItemCount = itemsPerSeller.get(sellerName);
                        if (sellerItemCount > maxSellerItems) {
                            System.out.println("The seller: " + sellerName + " has reached the max no. of items that he can submit");
                            return -1;
                        } else {
                            itemsPerSeller.put(sellerName, sellerItemCount + 1);
                            lastListingID++;
                            Item i = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
                            itemsAndIDs.put(lastListingID, i);
                            itemsUpForBidding.add(i);
                            highestBids.put(lastListingID, lowestBiddingPrice);
                            return lastListingID;
                        }
                    }
                } else {
                    return -1;
                }
            } else {

                if (biddingDurationCheck.containsKey(sellerName)) {
                    int count = biddingDurationCheck.get(sellerName);
                    biddingDurationCheck.put(sellerName, count + 1);
                } else {
                    biddingDurationCheck.put(sellerName, 1);
                }

                if (biddingDurationCheck.get(sellerName) >= 5) {
                    disqualifiedSellers.add(sellerName);
                }

                return -1;
            }
        } else {

            return -1;
        }

    }

    /**
     * Get all <code>Items</code> active in the auction
     *
     * @return A copy of the <code>List</code> of <code>Items</code>
     */
    /**
     * @Precondition: none
     * @Postcondition: return list of items currently listed as active
     * @Exception: none
     *
     */
    public synchronized List<Item> getItems() {
		// TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //    Don't forget that whatever you return is now outside of your control.

        //	Pseudo-code: getItems()
        //	Synchronized(itemsUpForBidding)
        //	Declare an array list of Item type variable called activeItems 
        //	For each Item in itemsUpForBidding
        // 		IF bidding is open for the item THEN
        //  		Add the item to the activeItems array list 
        // 		END IF
        //	END LOOP
        // 	return activeItems;
        ArrayList<Item> activeItems = new ArrayList<>();
        if (itemsUpForBidding.size() > 0) {
            for (Item i : itemsUpForBidding) {
                if (i.biddingOpen()) {
                    activeItems.add(i);
                }
            }
        }
        return activeItems;
    }

    /**
     * Attempt to submit a bid for an <code>Item</code>
     *
     * @param bidderName Name of the <code>Bidder</code>
     * @param listingID Unique ID of the <code>Item</code>
     * @param biddingAmount Total amount to bid
     * @return True if successfully bid, false otherwise
     */
    /**
     * @Precondition: biddingAmount must match the opening amount or exceed the
     * current bid amount listingID > 0 and must be unique
     * @Postcondition: return true if the bid is successfully submitted return
     * false if submission request is rejected
     * @Exception: none
     *
     */
    public synchronized boolean submitBid(String bidderName, int listingID, int biddingAmount) {
		// TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //   See if the item exists.
        //   See if it can be bid upon.
        //   See if this bidder has too many items in their bidding list.
        //   Get current bidding info.
        //   See if they already hold the highest bid.
        //   See if the new bid isn't better than the existing/opening bid floor.
        //   Decrement the former winning bidder's count
        //   Put your bid in place

        //	Pseudo-code: submitBid()
        //	Synchronized(this)
        //	IF the itemsAndIDs HashMap contains the given listingID THEN
        //		Get the item from the itemsAndIDs HashMap for the given listingID
        //		IF itemsUpForBidding list contains this item and the bidding for the item is open THEN
        //			IF the itemsPerBuyer HashMap contains the given bidderName THEN
        //				IF the count of itemsPerBuyer for the given bidder name > maxBidCount THEN
        //					return false;
        //				END IF
        //			ELSE
        //				Add the given bidderName to the itemsPerBuyer HashMap with the count of bids as 1
        //			END IF
        //			IF the highestBidders HashMap contains the given listingID and bidderName THEN
        //				return false;
        //			ELSE	
        //				IF the given biddingAmount > highest bid in highestBids for the given listingID THEN
        //					update the highestBidders HashMap with the given bidderName
        //					update the highestBids HashMap with the given bidderAmount
        //					update the count in the itemsPerBuyer HashMap for the given bidderName by 1
        //					return true;
        //				ELSE
        //					return false;
        //				END IF
        //			END IF
        //		ELSE
        //			return false;
        //		END IF	
        //	ELSE
        //		return false;
        //	END IF
        if (itemsAndIDs.containsKey(listingID)) {
            Item i = itemsAndIDs.get(listingID);
            if (itemsUpForBidding.contains(i) && i.biddingOpen()) {
                if (itemsPerBuyer.containsKey(bidderName)) {
                    if (itemsPerBuyer.get(bidderName) > maxBidCount) {
                        return false;
                    }
                } else {
                    itemsPerBuyer.put(bidderName, 1);
                }
                if (highestBidders.get(listingID) == bidderName) {
                    return false;
                } else if (highestBids.get(listingID) > biddingAmount) {
                    return false;
                } else {
                    highestBidders.remove(listingID);
                    highestBidders.put(listingID, bidderName);
                    highestBids.remove(listingID);
                    highestBids.put(listingID, biddingAmount);
                    int itemsPerBuyerCount = itemsPerBuyer.get(bidderName);
                    itemsPerBuyer.remove(bidderName);
                    itemsPerBuyer.put(bidderName, itemsPerBuyerCount + 1);
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
     *
     * @param bidderName Name of <code>Bidder</code>
     * @param listingID Unique ID of the <code>Item</code>
     * @return 1 (success) if bid is over and this <code>Bidder</code> has
     * won<br>
     * 2 (open) if this <code>Item</code> is still up for auction<br>
     * 3 (failed) If this <code>Bidder</code> did not win or the
     * <code>Item</code> does not exist
     */
    /**
     * @Precondition: listingID > 0 and must be unique
     * @Postcondition: return 1 if the bidder has the highest bid and the item's
     * bidding duration has passed return 2 if the item is still receiving bids
     * return 3 if the bidding is over and the bidder did not win
     * @Exception: none
     *
     */
    public synchronized int checkBidStatus(String bidderName, int listingID) {
		// TODO: IMPLEMENT CODE HERE
        // Some reminders:
        //   If the bidding is closed, clean up for that item.
        //     Remove item from the list of things up for bidding.
        //     Decrease the count of items being bid on by the winning bidder if there was any...
        //     Update the number of open bids for this seller

        //	Pseudo-code: checkBidStatus()
        //	Synchronized(this)
        //	IF the given listingID is not present in the itemsAndIDs HashMap THEN
        //		return 3;
        //	ELSE
        //		Get the item for the given listingID from the itemsAndIDs HashMap
        //		IF bidding is open for the above item THEN
        //			return 2;
        //		ELSE
        //			If the bidderName equals the bidder name from the highestBidders HashMap for the given listingID THEN
        //				remove the item from the itemsUpForBidding list	
        //	        	remove the bidderName from the highestBidders HashMap
        //				update the no. of items for the seller in the itemsPerSeller HashMap
        //				update the no. of bids for the bidderName in the itemsPerBuyer HashMap
        //				return 1;
        //			ELSE
        //				return 3;
        //			END IF
        //		END IF
        //	END IF
        if (!(itemsAndIDs.containsKey(listingID))) {
            return 3;
        } else {
            Item i = itemsAndIDs.get(listingID);
            if (i.biddingOpen()) {
                return 2;
            } else {
                if (bidderName.equals(highestBidders.get(listingID))) {
                    itemsUpForBidding.remove(i);
                    highestBidders.remove(listingID);
                    int itemCount = itemsPerSeller.get(i.seller());
                    itemsPerSeller.put(i.seller(), itemCount - 1);
                    int bidCount = itemsPerBuyer.get(bidderName);
                    itemsPerBuyer.put(bidderName, bidCount - 1);
                    return 1;
                } else {
                    return 3;
                }
            }
        }
    }

    /**
     * Check the current bid for an <code>Item</code>
     *
     * @param listingID Unique ID of the <code>Item</code>
     * @return The highest bid so far or the opening price if no bid has been
     * made, -1 if no <code>Item</code> exists
     */
    /**
     * @Precondition: listingID > 0 and must be unique
     * @Postcondition: return the highest bid or return the opening bid supplied
     * by seller if no bids are made for the item return -1 if no item matches
     * the listingID
     * @Exception: none
     *
     */
    public int itemPrice(int listingID) {
		// TODO: IMPLEMENT CODE HERE

        //	Pseudo-code: itemPrice()
        //	Synchronized(highestBids)
        //	IF the highestBids HashMap contains an item for the given listingID THEN  
        //		return the highestBid or the opening price if no bid are made for the listingID
        //	ELSE
        //  	return -1
        //	END IF
        synchronized (highestBids) {
            if (highestBids.containsKey(listingID)) {
                return highestBids.get(listingID);
            }
        }
        return -1;
    }

    /**
     * Check whether an <code>Item</code> has been bid upon yet
     *
     * @param listingID Unique ID of the <code>Item</code>
     * @return True if there is no bid or the <code>Item</code> does not exist,
     * false otherwise
     */
    /**
     * @Precondition: listingID > 0 and must be unique
     * @Postcondition: return true if no bids has been been placed or there is
     * no item with the supplied listingID return false if bid is made for the
     * item with the supplied listingID
     * @Exception: none
     *
     */
    public synchronized Boolean itemUnbid(int listingID) {
		// TODO: IMPLEMENT CODE HERE

        //	Pseudo-code: itemUnbid()
        //	Synchronized(this)
        //	If the itemsAndIDs HashMap contains the given listingID THEN
        //		Get the item from itemsAndIDs for the listingID
        //		IF the itemsUpForBidding List contains the above item THEN
        //			IF the lowestBiddingPrice for the item == highestBid from highestBids HashMap for the given listingID THEN
        //				return true;
        //			ELSE
        //				return false;
        //          END IF
        //		ELSE
        //			return true;
        //		END IF
        //	ELSE
        //  	return true;
        //	END IF
        if (itemsAndIDs.containsKey(listingID)) {
            Item i = itemsAndIDs.get(listingID);
            if (itemsUpForBidding.contains(i)) {
                if (i.lowestBiddingPrice() == highestBids.get(listingID)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }
}
