package net.bither.preference;

import net.bither.bitherj.core.BitherjSettings;
import net.bither.bitherj.crypto.PasswordSeed;
import net.bither.bitherj.qrcode.QRCodeUtil;
import net.bither.bitherj.utils.Utils;
import net.bither.utils.ExchangeUtil;
import net.bither.utils.FileUtil;
import net.bither.utils.MarketUtil.MarketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public class UserPreference {


    private static final String USER_LANGUAGE_CODE = "languageCode";

    private static final String DOWNLOAD_SPV_FINISH = "download_spv_finish";
    private static final String APP_MODE = "app_mode";
    private static final String TRANSACTION_FEE_MODE = "transaction_fee_mode";
    private static final String BITHERJ_DONE_SYNC_FROM_SPV = "bitheri_done_sync_from_spv";
    private static final String PASSWORD_SEED = "password_seed";
    private static final String USER_AVATAR = "user_avatar";
    private static final String PREFS_KEY_LAST_VERSION = "last_version";
    private static final String DEFAULT_MARKET = "default_market";
    private static final String DEFAULT_EXCHANGE_RATE = "default_exchange_rate";

    private static final String FIRST_RUN_DIALOG_SHOWN = "first_run_dialog_shown";
    private static final String LOOK_AND_FEEL = "lookAndFeel";

    private static final String LAST_BACK_UP_PRIVATE_KEY_TIME = "last_back_up_private_key_time";
    private static final String QR_QUALITY = "qr_quality";

    private static UserPreference mInstance = new UserPreference();

    private Properties userPreferences;

    private static Logger log = LoggerFactory.getLogger(UserPreference.class);

    private UserPreference() {
        userPreferences = FileUtil.loadUserPreferences(FileUtil.USER_PROPERTIES_FILE_NAME);
    }

    public static UserPreference getInstance() {
        return mInstance;
    }

    public Properties getUserPreferences() {
        return userPreferences;
    }

    private int getInt(String key, int defaultValue) {
        String result = userPreferences.getProperty(key);
        if (Utils.isEmpty(result)) {
            return defaultValue;
        } else {
            if (Utils.isInteger(result)) {
                return Integer.valueOf(result);
            } else {
                return defaultValue;
            }
        }
    }

    private int getLong(String key, int defaultValue) {
        String result = userPreferences.getProperty(key);
        if (Utils.isEmpty(result)) {
            return defaultValue;
        } else {
            if (Utils.isInteger(result)) {
                return Integer.valueOf(result);
            } else {
                return defaultValue;
            }
        }
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        String result = userPreferences.getProperty(key);
        if (Utils.isEmpty(result)) {
            return defaultValue;
        } else {
            return Boolean.valueOf(result);
        }
    }

    private String getString(String key, String defaultValue) {
        String result = userPreferences.getProperty(key);
        if (Utils.isEmpty(result)) {
            return defaultValue;
        } else {
            return result;
        }
    }

    public BitherjSettings.AppMode getAppMode() {
        int index = getInt(APP_MODE, -1);
        if (index < 0 || index >= BitherjSettings.AppMode.values().length) {
            return null;
        }
        return BitherjSettings.AppMode.values()[index];
    }

    private void setValue(String key, String value) {
        userPreferences.setProperty(key, value);
        saveUserPreferences();
    }

    public void setAppMode(BitherjSettings.AppMode mode) {
        int index = -1;
        if (mode != null) {
            index = mode.ordinal();
        }
        setValue(APP_MODE, Integer.toString(index));


    }

    public boolean getBitherjDoneSyncFromSpv() {
        return getBoolean(BITHERJ_DONE_SYNC_FROM_SPV, false);
    }

    public void setBitherjDoneSyncFromSpv(boolean isDone) {
        setValue(BITHERJ_DONE_SYNC_FROM_SPV, Boolean.toString(isDone));

    }

    public BitherjSettings.TransactionFeeMode getTransactionFeeMode() {
        int ordinal = getInt(TRANSACTION_FEE_MODE, 0);
        if (ordinal < BitherjSettings.TransactionFeeMode.values().length && ordinal >= 0) {
            return BitherjSettings.TransactionFeeMode.values()[ordinal];
        }
        return BitherjSettings.TransactionFeeMode.Normal;
    }

    public void setTransactionFeeMode(BitherjSettings.TransactionFeeMode mode) {
        if (mode == null) {
            mode = BitherjSettings.TransactionFeeMode.Normal;
        }
        setValue(TRANSACTION_FEE_MODE, Integer.toString(mode.ordinal()));

    }

    public boolean getDownloadSpvFinish() {
        return getBoolean(DOWNLOAD_SPV_FINISH, false);
    }

    public void setDownloadSpvFinish(boolean finish) {
        setValue(DOWNLOAD_SPV_FINISH, Boolean.toString(finish));
    }


    public PasswordSeed getPasswordSeed() {
        String str = getString(PASSWORD_SEED, "");
        if (Utils.isEmpty(str)) {
            return null;
        }
        return new PasswordSeed(str);
    }

    public void setPasswordSeed(PasswordSeed passwordSeed) {
        setValue(PASSWORD_SEED, passwordSeed.toPasswordSeedString());


    }

    public int getVerionCode() {
        return getInt(PREFS_KEY_LAST_VERSION, 0);
    }

    public void setVerionCode(int versionCode) {
        setValue(PREFS_KEY_LAST_VERSION, Integer.toString(versionCode));
    }

    public MarketType getDefaultMarket() {
        MarketType marketType = getMarketType();
        if (marketType == null) {
            setDefault();
        }
        marketType = getMarketType();
        return marketType;

    }

    private MarketType getMarketType() {
        int type = getInt(DEFAULT_MARKET, -1);
        if (type == -1) {
            return null;
        }
        return MarketType.values()[type];

    }

    public void setMarketType(MarketType marketType) {
        setValue(DEFAULT_MARKET, Integer.toString(marketType.ordinal()));
    }


    public boolean hasUserAvatar() {
        return !Utils.isEmpty(getUserAvatar());
    }

    public String getUserAvatar() {
        return getString(USER_AVATAR, "");
    }

    public void setUserAvatar(String avatar) {
        setValue(USER_AVATAR, avatar);
    }


    public boolean getFirstRunDialogShown() {
        return getBoolean(FIRST_RUN_DIALOG_SHOWN, false);
    }

    public void setFirstRunDialogShown(boolean shown) {
        setValue(FIRST_RUN_DIALOG_SHOWN, Boolean.toString(shown));
    }


    private void setUserPreference(String key, String value) {
        if (key != null && value != null) {
            userPreferences.put(key, value);
        }
    }


    public void remove(String key) {
        userPreferences.remove(key);
    }


    private void setDefault() {
        String defaultCountry = Locale.getDefault().getCountry();
        if (Utils.compareString(defaultCountry, "CN") || Utils.compareString
                (defaultCountry, "cn")) {
            setMarketType(MarketType.HUOBI);
        } else {
            setMarketType(MarketType.BITSTAMP);
        }
        String currencyCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        if (Utils.compareString(currencyCode, "CNY")) {
            setExchangeCurrency(ExchangeUtil.Currency.CNY);
        } else if (Utils.compareString(currencyCode, "EUR")) {
            setExchangeCurrency(ExchangeUtil.Currency.EUR);
        } else if (Utils.compareString(currencyCode, "GBP")) {
            setExchangeCurrency(ExchangeUtil.Currency.GBP);
        } else if (Utils.compareString(currencyCode, "JPY")) {
            setExchangeCurrency(ExchangeUtil.Currency.JPY);
        } else if (Utils.compareString(currencyCode, "KRW")) {
            setExchangeCurrency(ExchangeUtil.Currency.KRW);
        } else if (Utils.compareString(currencyCode, "CAD")) {
            setExchangeCurrency(ExchangeUtil.Currency.CAD);
        } else if (Utils.compareString(currencyCode, "AUD")) {
            setExchangeCurrency(ExchangeUtil.Currency.AUD);
        } else {
            setExchangeCurrency(ExchangeUtil.Currency.USD);
        }
    }

    public ExchangeUtil.Currency getDefaultCurrency() {
        ExchangeUtil.Currency currency = getExchangeType();
        if (currency == null) {
            setDefault();
        }
        currency = getExchangeType();
        return currency;

    }

    private ExchangeUtil.Currency getExchangeType() {
        int type = getInt(DEFAULT_EXCHANGE_RATE, -1);
        if (type == -1) {
            return null;
        }
        return ExchangeUtil.Currency.values()[type];

    }

    public void setExchangeCurrency(ExchangeUtil.Currency currency) {
        setValue(DEFAULT_EXCHANGE_RATE, Integer.toString(currency.ordinal()));
    }

    public void setUserLanguageCode(String languageCode) {
        setValue(USER_LANGUAGE_CODE, languageCode);

    }

    public String getUserLanguageCode() {
        return getString(USER_LANGUAGE_CODE, "en");

    }

    public void setLookAndFeel(String lookAndFeel) {
        setValue(LOOK_AND_FEEL, lookAndFeel);
    }

    public String getLookAndFeel() {
        return getString(LOOK_AND_FEEL, null);
    }


    public Date getLastBackupkeyTime() {
        Date date = null;
        long time = getLong(LAST_BACK_UP_PRIVATE_KEY_TIME, 0);
        if (time > 0) {
            date = new Date(time);
        }
        return date;
    }

    public void setLastBackupKeyTime(Date date) {
        if (date != null) {
            setValue(LAST_BACK_UP_PRIVATE_KEY_TIME, Long.toString(date.getTime()));

        }
    }

    public synchronized void saveUserPreferences() {
        FileUtil.saveUserPreferences(userPreferences, FileUtil.USER_PROPERTIES_FILE_NAME);

    }

    public QRCodeUtil.QRQuality getQRQuality() {
        int ordinal = getInt(QR_QUALITY, -1);
        if (ordinal < QRCodeUtil.QRQuality.values().length && ordinal >= 0) {
            return QRCodeUtil.QRQuality.values()[ordinal];
        }
        return QRCodeUtil.QRQuality.Normal;

    }

    public void setQRQuality(QRCodeUtil.QRQuality qrQuality) {
        int index = -1;
        if (qrQuality != null) {
            index = qrQuality.ordinal();
        }
        setValue(QR_QUALITY, Integer.toString(index));


    }
}
