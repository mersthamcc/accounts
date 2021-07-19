package cricket.merstham.website.accounts.configuration;

public class UserConfiguration {

    private String eposUser;
    private String eposPassword;

    public String getEposUser() {
        return eposUser;
    }

    public UserConfiguration setEposUser(String eposUser) {
        this.eposUser = eposUser;
        return this;
    }

    public String getEposPassword() {
        return eposPassword;
    }

    public UserConfiguration setEposPassword(String eposPassword) {
        this.eposPassword = eposPassword;
        return this;
    }
}
