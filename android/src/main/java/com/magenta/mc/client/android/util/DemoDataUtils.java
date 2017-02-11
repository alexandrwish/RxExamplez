package com.magenta.mc.client.android.util;

import android.util.Pair;

import com.magenta.maxunits.mobile.entity.Address;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DemoDataUtils {

    public static final Random RAND = new Random(System.currentTimeMillis());
    public final static PhoneNumberGenerator DEFAULT_PHONE_NUMBER_GENERATOR = new PhoneNumberGenerator();

    public static final String[] FIRST_NAME = new String[]{
            "Edmund", "William", "Edith", "Pamela", "Dorris", "Lisa", "Fred", "Sarah", "Vera", "Lillian", "Margarett",
            "John", "Mike", "Gareth", "Agata", "Richard", "Brian", "Martin", "Stella", "Sonia", "Dawn", "Marcus",
            "David", "James", "Frank", "Margory", "Adam", "Lavinia", "Michael", "Alex", "Chris", "Anna", "Hyacinth",
            "Chun", "Nigel"};

    public static final String[] LAST_NAME = new String[]{
            "Harvey", "Smith", "Ferguson", "Oliveri", "Ballard", "Mansell", "Strange", "Gownwater", "Goslin", "Rook",
            "Cholenski", "Spencer", "Wong", "Brundle", "Blackadder", "Stansfield", "Rogers", "Summers", "Bruce-Kerr",
            "Corderoy", "Pay", "Bucket", "Bloggs", "Bourgaite", "Benjamin", "Bone", "Rankin", "Lewis", "Hanson", "Hunt",
            "Abley", "Benedict", "Wallader", "Carrodus", "Rail"};

    public final static double[][] COORDINATE_RANDOM_RU_SAMARA = new double[][]{
            new double[]{53.226629, 50.181577},
            new double[]{53.215876, 50.200524},
            new double[]{53.205698, 50.175076},
            new double[]{53.194464, 50.123363},
            new double[]{53.185773, 50.089588},
            new double[]{53.183999, 50.145807},
            new double[]{53.196161, 50.1828},
            new double[]{53.206984, 50.222282},
            new double[]{53.227284, 50.268588},
            new double[]{53.240718, 50.300946},
            new double[]{53.262851, 50.302663},
            new double[]{53.273734, 50.31601},
            new double[]{53.274606, 50.274596},
    };

    public final static String LONG_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed vel " +
            "imperdiet nisi. Proin eros est, vehicula nec convallis facilisis, porta nec arcu. Pellentesque auctor " +
            "est nec metus pharetra non eleifend risus tempor. Vestibulum ante ipsum primis in faucibus orci luctus " +
            "et ultrices posuere cubilia Curae; Vestibulum orci erat, fringilla sed accumsan ut, rhoncus sit amet " +
            "augue. Sed velit purus, suscipit ac pulvinar vitae, malesuada sed lacus. Proin a mauris eu metus " +
            "hendrerit pretium eget et neque. Donec sed dui ac purus tincidunt hendrerit. Cras odio turpis, " +
            "convallis eu sollicitudin in, tempor et erat. Cras vel tempor mauris. Ut in quam lorem, et elementum " +
            "tortor. Nunc et ligula a nisl feugiat malesuada. Praesent fermentum accumsan nunc, vel adipiscing " +
            "lectus semper sit amet.";

    public static final String[] PARCEL_TYPE = new String[]{"Consumer Electronics", "Appliances", "Furniture", "Fitness Equipment", "Other"};
    public static final String[] WORK_TYPES = new String[]{"Repair", "Installation", "Dismantling", "Routine maintenance"};
    public static final String[] PARCEL_DELIVERY_NOTES = new String[]{
            "The entrance from the backyard",
            "The customer have an angry dog",
            "The customer asked to ring 3 times",
            "Courier must come alone and unarmed",
            "Must be delivered without delay",
    };

    static {
        Country.GB.putTown(Town.LONDON, new String[]{
                        "1 Pall Mall E, London, Greater London SW1Y 5AU, UK",
                        "6-7 John Adam St, City of Westminster, WC2N, UK",
                        "14 Mercer St, City of Westminster, London WC2H 9QE, UK",
                        "23 Old Bond St, City of Westminster, W1S, UK",
                        "22 Hollen St, London, Greater London W1F 8BG, UK",
                        "122 Great Titchfield St, London, Greater London W1W 6ST, UK",
                        "1 Bickenhall St, City of Westminster, W1U, UK",
                        "Newcastle Pl, City of Westminster, W2, UK",
                        "46 Lodge Rd, Saint John's Wood Substation, London, Greater London NW8 8NZ, UK",
                        "40 Ashworth Rd, London, Greater London W9 1JY, UK",
                        "74 Ashmore Rd, Paddington, City of Westminster, W9, UK",
                        "13 Blake Close, London, Greater London W10 6AY, UK",
                        "5 Du Cane Rd, White City, London Borough of Hammersmith and Fulham, London W12, UK",
                        "102 Nutbourne St, City of Westminster, London W10, UK",
                        "16 Marsland Close, London, Greater London SE17 3JW, UK",
                        "Barry House, London Borough of Southwark, SE16, UK",
                        "Nazareth Close, London Borough of Southwark, SE15, UK",
                        "Petworth House, Dog Kennel Hill Estate, London, Greater London SE22 8DD, UK",
                        "22 St Saviour's Rd, London Borough of Lambeth, SW2, UK",
                        "18 De Montfort Rd, London, Greater London SW16 1LZ, UK",
                        "45 St Gothard Rd, Lambeth, London Borough of Lambeth, SE27, UK",
                        "Christine Court, Lawrie Park Crescent, Beckenham, London, Greater London SE26 6HY, UK",
                        "2-5 Kelsey Ln, Beckenham, Greater London BR3, UK",
                        "7-9 Simpson's Rd, Bromley, Kent BR2 9AP, UK",
                        "5 Marlings Park Ave, Orpington, Greater London BR7, UK"},
                new double[][]{
                        {51.5080308, -0.1307534},
                        {51.5096049, -0.1219894},
                        {51.5129412, -0.1259981},
                        {51.5092032, -0.1411794},
                        {51.5202009, -0.1411379},
                        {51.5212952, -0.1577945},
                        {51.520482, -0.1722695},
                        {51.5281507, -0.1704084},
                        {51.5284059, -0.1856111},
                        {51.5274003, -0.2029928},
                        {51.5217697, -0.2238141},
                        {51.5163872, -0.2345631},
                        {51.5308088, -0.2128463},
                        {51.4873448, -0.100476},
                        {51.4901759, -0.0583279},
                        {51.4701444, -0.062504},
                        {51.4635641, -0.0818524},
                        {51.4635641, -0.0818524},
                        {51.4548185, -0.1209812},
                        {51.4371431, -0.1307138},
                        {51.430107, -0.0935856},
                        {51.4229613, -0.0588561},
                        {51.4048712, -0.0282862},
                        {51.3990946, 0.0156691},
                        {51.3991202, 0.0935069}
                },
                new PhoneNumberGenerator() {
                    public String next() {
                        return String.format(Locale.UK, "+44 20 %04d %04d", RAND.nextInt(9999), RAND.nextInt(9999));
                    }
                });

        Country.RU.putTown(Town.SAMARA, new String[]{
                        "ulitsa Michurina, 74, Samara, Samarskaya oblast', Russia",
                        "Prospekt Maslennikova, 19, Samara, Samarskaya oblast', Russia",
                        "prospekt Karla Marksa, 165Б, Samara, Samarskaya oblast', Russia",
                        "Revolyutsionnaya ulitsa, 70, Samara, Samarskaya oblast', Russia",
                        "ulitsa Ivana Bulkina, 85, Samara, Samarskaya oblast', Russia",
                        "ulitsa Mikhaila Sorokina, 13, Samara, Samarskaya oblast', Russia",
                        "Ulitsa Antonova-Ovseenko, 89, Samara, Samarskaya oblast', Russia",
                        "Srednesadovaya ulitsa, 14, Samara, Samarskaya oblast', Russia",
                        "Ryl'skaya ulitsa, 34, Samara, Samarskaya oblast', Russia",
                        "Kabel'naya ulitsa, 8, Samara, Samarskaya oblast', Russia",
                        "Shchigrovskiy pereulok, 12, Samara, Samarskaya oblast', Russia",
                        "Ulitsa Zemetsa, 32, Samara, Samarskaya oblast', Russia",
                        "Gornaya ulitsa, 2, Samara, Samarskaya oblast', Russia",
                        "Moskovskoye shosse, 252, Samara, Samarskaya oblast', Russia",
                        "Prospekt Kirova, 393, Samara, Samarskaya oblast', Russia",
                        "ulitsa Georgiya Dimitrova, 74, Samara, Samarskaya oblast', Russia",
                        "Ulitsa Stara-Zagora, 204, Samara, Samarskaya oblast', Russia",
                        "Tashkentskaya ulitsa, 109, Samara, Samarskaya oblast', Russia",
                        "Cheremshanskaya ulitsa, 222, Samara, Samarskaya oblast', Russia",
                        "Eniseyskaya ulitsa, 43, Samara, Samarskaya oblast', Russia",
                        "ulitsa Metallistov, 17, Samara, Samarskaya oblast', Russia",
                        "Fizkul'turnaya ulitsa, 119, Samara, Samarskaya oblast', Russia",
                        "Prospekt Kirova, 75, Samara, Samarskaya oblast', Russia",
                        "ulitsa Litvinova, 258, Samara, Samarskaya oblast', Russia",
                        "Tsekhovaya ulitsa, 181, Samara, Samarskaya oblast', Russia"},
                new double[][]{
                        {53.2055092, 50.1430092},
                        {53.2112389, 50.1552315},
                        {53.2038689, 50.1665497},
                        {53.2067909, 50.1779594},
                        {53.2136192, 50.1936302},
                        {53.2020683, 50.2064209},
                        {53.2123489, 50.2216911},
                        {53.2096291, 50.2374001},
                        {53.2015114, 50.2470703},
                        {53.1910591, 50.2676582},
                        {53.2073708, 50.284359},
                        {53.2083206, 50.3027802},
                        {53.1848106, 50.1502304},
                        {53.2443008, 50.2106895},
                        {53.2505684, 50.2252617},
                        {53.2522697, 50.2300682},
                        {53.2520714, 50.2455788},
                        {53.2477417, 50.2528305},
                        {53.242939, 50.2599792},
                        {53.2332802, 50.2704391},
                        {53.2244797, 50.2701683},
                        {53.2244797, 50.2701683},
                        {53.2188911, 50.2676315},
                        {53.2037392, 50.2829819},
                        {53.2312126, 50.3111801},
                        {53.2486382, 50.3128319}
                },
                new PhoneNumberGenerator() {
                    public String next() {
                        return String.format(Locale.UK, "+7 8462 %05d", RAND.nextInt(9999));
                    }
                });
    }

    private DemoDataUtils() {
    }

    public static String randomFullName() {
        return FIRST_NAME[RAND.nextInt(FIRST_NAME.length)] + " " + LAST_NAME[RAND.nextInt(LAST_NAME.length)];
    }

    public static String randomAddress(final Country country, final Town town) {
        final Country.TownData townData = country.getTown(town);
        if (townData == null) {
            throw new IllegalArgumentException("Town (" + town + ") of country (" + country + ") not found");
        }
        return townData.addresses[RAND.nextInt(townData.addresses.length)];
    }

    public static double[] randomLocation(final Country country, final Town town, String address) {
        final Country.TownData townData = country.getTown(town);

        if (townData == null) {
            throw new IllegalArgumentException("Town (" + town + ") of country (" + country + ") not found");
        }

        for (int i = 0; i < townData.addresses.length; i++) {
            if (townData.addresses[i].equalsIgnoreCase(address)) {
                return townData.locations[i];
            }
        }
        return null;
    }

    public static Address randomAddressAndLocation(final Country country, final Town town) {
        final Country.TownData townData = country.getTown(town);
        if (townData == null) {
            throw new IllegalArgumentException("Town (" + town + ") of country (" + country + ") not found");
        }
        final int index = RAND.nextInt(townData.addresses.length);
        final Address address = new Address(townData.addresses[index], null);
        final double[] location = townData.locations[index];
        address.setLatitude(location[0]);
        address.setLongitude(location[1]);
        return address;
    }

    public static String makeRandomString() {
        return makeRandomString(80);
    }

    public static String makeRandomString(final int minLength) {
        return LONG_TEXT.substring(0, Math.max(RAND.nextInt(LONG_TEXT.length()), minLength));
    }

    public static String randomPhone() {
        return DEFAULT_PHONE_NUMBER_GENERATOR.next();
    }

    public static String randomPhone(final Country country, final Town town) {
        final Country.TownData townData = country.getTown(town);
        if (townData == null) {
            throw new IllegalArgumentException("Town (" + town + ") of country (" + country + ") not found");
        }
        return townData.phoneNumberGenerator.next();
    }

    public static String randomWorkType() {
        return WORK_TYPES[RAND.nextInt(WORK_TYPES.length)];
    }

    public static String randomParcelType() {
        return PARCEL_TYPE[RAND.nextInt(PARCEL_TYPE.length)];
    }

    public static String randomString(final Random random) {
        return LONG_TEXT.substring(0, Math.max(random.nextInt(LONG_TEXT.length()), 80));
    }

    public static String makeRandomString(final String[] values) {
        final StringBuilder sb = new StringBuilder();
        final int count = (int) (Math.random() * values.length);
        for (int i = 0; i < count; i++) {
            sb.append(values[((int) (Math.random() * (values.length - 1)))]).append(" ");
        }
        return sb.toString();
    }

    public static String randomOptionsString(final String[] values) {
        final StringBuilder sb = new StringBuilder();
        final Set<Integer> skip = new HashSet<Integer>();
        for (int i = 0; i < Math.max(1, RAND.nextInt(values.length)); i++) {
            final int next = RAND.nextInt(values.length);
            if (!skip.contains(next)) {
                sb.append(values[next]).append(" ");
                skip.add(next);
            }
        }
        return sb.toString();
    }

    public static String randomParcelDeliveryNotes(final boolean allowEmpty) {
        return allowEmpty && RAND.nextInt() % 3 == 0 ? "" : PARCEL_DELIVERY_NOTES[RAND.nextInt(PARCEL_DELIVERY_NOTES.length)];
    }

    /**
     * @param name ex. Samara, RU
     * @return first - country, second - town
     */
    public static Pair<Country, Town> parseCountryAndTown(final String name) {
        final String[] parts = name.split(",");
        return new Pair<>(parts.length > 1 ? Country.valueOf(parts[1].trim().toUpperCase()) : null, parts.length > 0 ? Town.valueOf(parts[0].trim().toUpperCase()) : null);
    }

    public enum Country {
        RU,
        GB;

        public final Map<Town, TownData> townMap = new HashMap<>(0);

        public Country putTown(
                final Town name,
                final String[] addresses,
                final double[][] locations,
                final PhoneNumberGenerator phoneNumberGenerator) {
            this.townMap.put(name, new TownData(addresses, locations, phoneNumberGenerator));
            return this;
        }

        public Country putTown(final Town name, final String[] addresses, final double[][] locations) {
            return putTown(name, addresses, locations, DEFAULT_PHONE_NUMBER_GENERATOR);
        }

        public TownData getTown(final Town town) {
            return townMap.get(town);
        }

        public static final class TownData {

            public final String[] addresses;
            public final double[][] locations;
            public final PhoneNumberGenerator phoneNumberGenerator;

            public TownData(
                    final String[] addresses,
                    final double[][] locations,
                    final PhoneNumberGenerator phoneNumberGenerator) {
                this.addresses = addresses;
                this.locations = locations;
                this.phoneNumberGenerator = phoneNumberGenerator;
            }
        }
    }

    public enum Town {
        SAMARA,
        LONDON
    }

    private static class PhoneNumberGenerator {

        public String next() {
            return String.format(Locale.UK, "+%04d %04d %04d", RAND.nextInt(9999), RAND.nextInt(9999), RAND.nextInt(9999));
        }
    }
}