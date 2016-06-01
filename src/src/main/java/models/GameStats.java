package models;

/**
 * Statistics bean.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-31.
 */
public class GameStats {

    private String prevWins, prevDefeats, prevDraws, avgPoints, avgPointsGiven, avgMoves, avgGridSize, totPoints, totPointsGiven, totWins, totDefeats, totDraws, oppName, oppSurname;

    public GameStats() {}

    public String getPrevWins() {
        return prevWins;
    }

    public void setPrevWins(String prevWins) {
        this.prevWins = prevWins;
    }

    public String getPrevDefeats() {
        return prevDefeats;
    }

    public void setPrevDefeats(String prevDefeats) {
        this.prevDefeats = prevDefeats;
    }

    public String getPrevDraws() {
        return prevDraws;
    }

    public void setPrevDraws(String prevDraws) {
        this.prevDraws = prevDraws;
    }

    public String getAvgPoints() {
        return avgPoints;
    }

    public void setAvgPoints(String avgPoints) {
        this.avgPoints = avgPoints;
    }

    public String getAvgPointsGiven() {
        return avgPointsGiven;
    }

    public void setAvgPointsGiven(String avgPointsGiven) {
        this.avgPointsGiven = avgPointsGiven;
    }

    public String getAvgMoves() {
        return avgMoves;
    }

    public void setAvgMoves(String avgMoves) {
        this.avgMoves = avgMoves;
    }

    public String getAvgGridSize() {
        return avgGridSize;
    }

    public void setAvgGridSize(String avgGridSize) {
        this.avgGridSize = avgGridSize;
    }

    public String getTotPoints() {
        return totPoints;
    }

    public void setTotPoints(String totPoints) {
        this.totPoints = totPoints;
    }

    public String getTotPointsGiven() {
        return totPointsGiven;
    }

    public void setTotPointsGiven(String totPointsGiven) {
        this.totPointsGiven = totPointsGiven;
    }

    public String getOppName() {
        return oppName;
    }

    public void setOppName(String oppName) {
        this.oppName = oppName;
    }

    public String getOppSurname() {
        return oppSurname;
    }

    public void setOppSurname(String oppSurname) {
        this.oppSurname = oppSurname;
    }

    public String getTotWins() {
        return totWins;
    }

    public void setTotWins(String totWins) {
        this.totWins = totWins;
    }

    public String getTotDefeats() {
        return totDefeats;
    }

    public void setTotDefeats(String totDefeats) {
        this.totDefeats = totDefeats;
    }

    public String getTotDraws() {
        return totDraws;
    }

    public void setTotDraws(String totDraws) {
        this.totDraws = totDraws;
    }
}
