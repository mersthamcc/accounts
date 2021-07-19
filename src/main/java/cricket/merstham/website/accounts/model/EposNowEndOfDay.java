package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EposNowEndOfDay {

    @JsonProperty("Id")
    @JsonAlias("EndOfDayID")
    private long id;

    @JsonProperty("StartTime")
    private LocalDateTime startTime;

    @JsonProperty("EndTime")
    private LocalDateTime endTime;

    @JsonProperty("Float")
    private BigDecimal endOfDayFloat;

    @JsonProperty("Total")
    private BigDecimal total;

    @JsonProperty("InTill")
    private BigDecimal inTill;

    @JsonProperty("DeviceID")
    private long deviceID;

    @JsonProperty("StaffOpen")
    private long staffOpen;

    @JsonProperty("StaffClose")
    private long staffClose;

    public long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getEndOfDayFloat() {
        return endOfDayFloat;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getInTill() {
        return inTill;
    }

    public long getDeviceID() {
        return deviceID;
    }

    public long getStaffOpen() {
        return staffOpen;
    }

    public long getStaffClose() {
        return staffClose;
    }
}
