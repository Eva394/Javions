package ch.epfl.javions.adsb;


import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class RawMessageTest {
    //TODO do tests

    public static final int RAW_MESSAGE_LENGTH = 14;
    public byte[] validBytes;
    public ByteString validByteString;
    public RawMessage rawMessage;
    public long validHorodatage;


    @BeforeEach
    void setUp() {
        validBytes = new byte[RAW_MESSAGE_LENGTH];
        validByteString = new ByteString( validBytes );
        validHorodatage = 100;

        rawMessage = new RawMessage( validHorodatage, validByteString );
    }


    @Test
    void testConstructorThrowsOnNegativeHorodatage() {
        long invalidHorodatage = -1;

        assertThrows( IllegalArgumentException.class, () -> new RawMessage( invalidHorodatage, validByteString ) );
    }


    @Test
    void testConstructorThrowsOnInvalidByteArrayLentgh() {
        byte[] invalidBytes = new byte[RAW_MESSAGE_LENGTH - 1];
        ByteString invalidByteString = new ByteString( invalidBytes );
        long validHorodatage = 10;

        assertThrows( IllegalArgumentException.class, () -> new RawMessage( validHorodatage, invalidByteString ) );
    }


    @Test
    void testConstructorDoenstThrowOnValidArguments() {

        assertDoesNotThrow( () -> new RawMessage( validHorodatage, validByteString ) );

        long veryBigHorodatage = 1L << 63 - 1;
        assertDoesNotThrow( () -> new RawMessage( veryBigHorodatage, validByteString ) );

        long nullHorodatage = 0;
        assertDoesNotThrow( () -> new RawMessage( nullHorodatage, validByteString ) );
    }


    @Test
    void testLengthVariableIsCorrect() {

        assertEquals( 14, RawMessage.LENGTH );
    }


    @Test
    void testOfReturnsTheCorrectMessageAndHorodatage() {
        byte[][] bytes = new byte[][]{
                {(byte)0x8D, (byte)0x39, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x03, (byte)0x5D, (byte)0xB8},
                {(byte)0x8D, (byte)0xFC, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x18, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x01, (byte)0x5D, (byte)0xB8},
                {(byte)0x8D, (byte)0x99, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7B, (byte)0xB4,
                 (byte)0xC0, (byte)0x04, (byte)0x29, (byte)0x03, (byte)0x5D, (byte)0x98},
                {(byte)0x8D, (byte)0xB1, (byte)0x2A, (byte)0x64, (byte)0x19, (byte)0x10, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x01, (byte)0x5C, (byte)0xB8},
                {(byte)0x8D, (byte)0xBA, (byte)0x0A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xA5,
                 (byte)0xC0, (byte)0x84, (byte)0x39, (byte)0x03, (byte)0x5D, (byte)0xB8}};

        ByteString[] expectedByteString = new ByteString[bytes.length];

        for ( int i = 0 ; i < expectedByteString.length ; i++ ) {
            expectedByteString[i] = new ByteString( bytes[i] );
        }

        for ( int i = 0 ; i < bytes.length ; i++ ) {
            RawMessage expectedRawMessage = new RawMessage( validHorodatage, expectedByteString[i] );
            RawMessage actualRawMessage = RawMessage.of( validHorodatage, bytes[i] );

            assertEquals( expectedRawMessage, actualRawMessage );
        }
    }


    @Test
    void testOfRetrunsNullForInvalidCrc() {
        byte[][] bytes = new byte[][]{
                {(byte)0x8D, (byte)0x56, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x0A, (byte)0x03, (byte)0x5D, (byte)0xB8},
                {(byte)0x8D, (byte)0xFC, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x18, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x34, (byte)0x01, (byte)0x8D, (byte)0xB8},
                {(byte)0x8D, (byte)0x99, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7B, (byte)0xB4,
                 (byte)0xC0, (byte)0x04, (byte)0x29, (byte)0x03, (byte)0x6D, (byte)0x4E},
                {(byte)0x67, (byte)0xB1, (byte)0x3A, (byte)0x64, (byte)0x19, (byte)0x10, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x01, (byte)0x5D, (byte)0x28},
                {(byte)0x8D, (byte)0xBB, (byte)0x0A, (byte)0xE4, (byte)0x49, (byte)0x10, (byte)0x7F, (byte)0xA5,
                 (byte)0xC0, (byte)0x84, (byte)0x79, (byte)0x03, (byte)0x5F, (byte)0xB8}};

        for ( int i = 0 ; i < bytes.length ; i++ ) {
            RawMessage actualRawMessage = RawMessage.of( validHorodatage, bytes[i] );

            assertEquals( null, actualRawMessage );
        }
    }


    @Test
    void testSizeReturnsZeroIfInvalidDf() {
        byte theByte = 0x00;
        for ( int i = 0 ; i < ( 2 * Byte.SIZE ) * ( 2 * Byte.SIZE ) ; i++ ) {
            if ( theByte != 0x11 ) {
                assertEquals( 0, rawMessage.size( theByte ) );
                theByte++;
            }
        }
    }


    @Test
    void testSizeReturnsCorrectLengthForValidDf() {
        int expectedValue = 17;
        byte theByte = 0x11;
        assertEquals( expectedValue, theByte );
    }


    @Test
    void testTypeCodeReturnsCorrectValue() {
        byte[] expectedValues = new byte[]{(byte)0b10011, (byte)0b10011, (byte)0b10011, (byte)0b00011, (byte)0b10011};
        byte[][] bytes = new byte[][]{
                {(byte)0x8D, (byte)0x39, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x03, (byte)0x5D, (byte)0xB8},
                {(byte)0x8D, (byte)0xFC, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x18, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x01, (byte)0x5D, (byte)0xB8},
                {(byte)0x8D, (byte)0x99, (byte)0x2A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7B, (byte)0xB4,
                 (byte)0xC0, (byte)0x04, (byte)0x29, (byte)0x03, (byte)0x5D, (byte)0x98},
                {(byte)0x8D, (byte)0xB1, (byte)0x2A, (byte)0x64, (byte)0x19, (byte)0x10, (byte)0x7F, (byte)0xB5,
                 (byte)0xC0, (byte)0x04, (byte)0x39, (byte)0x01, (byte)0x5C, (byte)0xB8},
                {(byte)0x8D, (byte)0xBA, (byte)0x0A, (byte)0xE4, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xA5,
                 (byte)0xC0, (byte)0x84, (byte)0x39, (byte)0x03, (byte)0x5D, (byte)0xB8}};
        byte[][] payloads = new byte[][]{
                {(byte)0x00, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xB5, (byte)0xC0, (byte)0x04, (byte)0x39},
                {(byte)0x00, (byte)0x99, (byte)0x18, (byte)0x7F, (byte)0xB5, (byte)0xC0, (byte)0x04, (byte)0x39},
                {(byte)0x00, (byte)0x99, (byte)0x10, (byte)0x7B, (byte)0xB4, (byte)0xC0, (byte)0x04, (byte)0x29},
                {(byte)0x00, (byte)0x19, (byte)0x10, (byte)0x7F, (byte)0xB5, (byte)0xC0, (byte)0x04, (byte)0x39},
                {(byte)0x00, (byte)0x99, (byte)0x10, (byte)0x7F, (byte)0xA5, (byte)0xC0, (byte)0x84, (byte)0x39}};

        for ( int i = 0 ; i < expectedValues.length ; i++ ) {

            ByteString byteString = new ByteString( payloads[i] );
            long payload = byteString.bytesInRange( 0, byteString.getBytes().length );

            int actualValue = RawMessage.typeCode( payload );
            assertEquals( expectedValues[i], actualValue );
        }
    }

    //array of bytes that have a crc equal to 0
    //8D392AE499107FB5C00439035DB8
    //8DFC2AE499187FB5C00439015DB8
    //8D992AE499107BB4C00429035D98
    //8DB12A6419107FB5C00439015CB8
    //8DBA0AE499107FA5C08439035DB8

    //samples from the prof
    //    RawMessage[timeStampNs=8096200, bytes=8D4B17E5F8210002004BB8B1F1AC]
    //    RawMessage[timeStampNs=75898000, bytes=8D49529958B302E6E15FA352306B]
    //    RawMessage[timeStampNs=100775400, bytes=8D39D300990CE72C70089058AD77]
    //    RawMessage[timeStampNs=116538700, bytes=8D4241A9601B32DA4367C4C3965E]
    //    RawMessage[timeStampNs=129268900, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]
    //    RawMessage[timeStampNs=138560100, bytes=8D4D222860B985F7F53FAB33CE76]
    //    RawMessage[timeStampNs=146689300, bytes=8D440237990D6D9FB8088CC99EC4]
    //    RawMessage[timeStampNs=208135700, bytes=8D4D029F594B52EFDB7E94ACEAC8]
    //    RawMessage[timeStampNs=208341000, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=210521800, bytes=8F01024C99256F1F78048C290D2D]
    //    RawMessage[timeStampNs=232125000, bytes=8D4B17E5990CB61068400CDD09D9]
    //    RawMessage[timeStampNs=233069800, bytes=8D3C648158AF92F723BC275EC692]
    //    RawMessage[timeStampNs=235839800, bytes=8D4952999915769CF02089DB69B1]
    //    RawMessage[timeStampNs=270464000, bytes=8D4D22289909451F10048C38343B]
    //    RawMessage[timeStampNs=285706800, bytes=8D4B2964EA234860015C00864171]
    //    RawMessage[timeStampNs=288802400, bytes=8D4B1900990DC48E380485203866]
    //    RawMessage[timeStampNs=316898700, bytes=8D4241A999086D0DD0840CDBACA6]
    //    RawMessage[timeStampNs=349256100, bytes=8D4B1A0058337639355B77835CBF]
    //    RawMessage[timeStampNs=349526700, bytes=8D4B1A00990CD99608480A2FD6D9]
    //    RawMessage[timeStampNs=373294000, bytes=8D4241A9EA11A898011C08B21C01]
    //    RawMessage[timeStampNs=379261900, bytes=8D4B1A00F8210002004BB8B7E02D]
    //    RawMessage[timeStampNs=385821900, bytes=8D39D300EA4A5867A53C089D5A75]
    //    RawMessage[timeStampNs=408163200, bytes=8D3C64819908E62EF0048B0C93B1]
    //    RawMessage[timeStampNs=429622400, bytes=8D4BCDE99915851930048B22CFAA]
    //    RawMessage[timeStampNs=445705200, bytes=8D4D029FF8132006005AB8D115B9]
    //    RawMessage[timeStampNs=470281500, bytes=8D4D029FEA1978667B5F042202CE]
    //    RawMessage[timeStampNs=493403000, bytes=8D495299EA447860015F8829B831]
    //    RawMessage[timeStampNs=500806100, bytes=8D39D300F82100020049B851B34D]
    //    RawMessage[timeStampNs=559063400, bytes=8D4B1900EA3E9860013C0897BF27]
    //    RawMessage[timeStampNs=587873700, bytes=8D4D222860B9827A1547B81799F2]
    //    RawMessage[timeStampNs=600759300, bytes=8D39D30058C382773D3FA0FB47B8]
    //    RawMessage[timeStampNs=612521000, bytes=8D4BCDE958B98681617391A02830]
    //    RawMessage[timeStampNs=633078600, bytes=8D3C648158AF92F741BC3075DA97]
    //    RawMessage[timeStampNs=636533100, bytes=8D4B29649910770C105008F4F234]
    //    RawMessage[timeStampNs=645590600, bytes=8D4D029F594B52EFC97E8B9546CA]
    //    RawMessage[timeStampNs=645795900, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=705844800, bytes=8D4952999915769CF02089DB69B1]
    //    RawMessage[timeStampNs=715212600, bytes=8D4D0221581362C8E95F06DFFD44]
    //    RawMessage[timeStampNs=773099400, bytes=8D3C6481EA42885C573C08357403]
    //    RawMessage[timeStampNs=797810600, bytes=8D4CA2BF606502FF0121D5D16090]
    //    RawMessage[timeStampNs=812236600, bytes=8D4951CE60738329CBB3E7A16B8B]
    //    RawMessage[timeStampNs=833094200, bytes=8D3C64819908E62EF0048B0C93B1]
    //    RawMessage[timeStampNs=857674200, bytes=8D39CEAA58AF865FDEF935606271]
    //    RawMessage[timeStampNs=858094400, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=868432200, bytes=8D495299E11B1100000000DDB0DF]
    //    RawMessage[timeStampNs=1008567300, bytes=8D49529958B302E6B95F836AEF91]
    //    RawMessage[timeStampNs=1032331100, bytes=8D4B17E5582522AD9155733599CF]
    //    RawMessage[timeStampNs=1063803000, bytes=8D495299F8230002004AB8F789C5]
    //    RawMessage[timeStampNs=1065855100, bytes=8DA4F2399915C318B0048AC5BDBE]
    //    RawMessage[timeStampNs=1127252300, bytes=8D4D222860B985F8273FCA1B84EA]
    //    RawMessage[timeStampNs=1138542200, bytes=8D4241A9601B4656B55F082C95CF]
    //    RawMessage[timeStampNs=1149118600, bytes=8D394C13E10E8B000000001CB2F5]
    //    RawMessage[timeStampNs=1164431700, bytes=8D4D22289909451F10088C706E3B]
    //    RawMessage[timeStampNs=1184750000, bytes=8D4D029F594B466BB3752CFB160C]
    //    RawMessage[timeStampNs=1184955200, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=1214082000, bytes=8D394C1360B505E402E63112F294]
    //    RawMessage[timeStampNs=1218171500, bytes=8D4B29649910770C105008F4F234]
    //    RawMessage[timeStampNs=1219038000, bytes=8D394C139908E1AFD8088A824A12]
    //    RawMessage[timeStampNs=1223069500, bytes=8D3C648158AF92F76FBC3E72A971]
    //    RawMessage[timeStampNs=1224093900, bytes=8D394C13EA447858013C08073C80]
    //    RawMessage[timeStampNs=1233094200, bytes=8D3C64819908E62EF0048B0C93B1]
    //    RawMessage[timeStampNs=1244978500, bytes=8D4D022158134645A1567E0FF5EB]
    //    RawMessage[timeStampNs=1280289300, bytes=8D4B17E5990CB21108400DCAAFC0]
    //    RawMessage[timeStampNs=1288753400, bytes=8D4952999915769CF02089DB69B1]
    //    RawMessage[timeStampNs=1323470700, bytes=8D4BCDE9EA466867497C08E62323]
    //    RawMessage[timeStampNs=1350181200, bytes=8D4CA2BFEA291866151C08123192]
    //    RawMessage[timeStampNs=1399281900, bytes=8D4B1A00583362BC33640F90AD74]
    //    RawMessage[timeStampNs=1399822900, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]
    //    RawMessage[timeStampNs=1422737500, bytes=8D4BCDE99915851930048B22CFAA]
    //    RawMessage[timeStampNs=1437501700, bytes=8D39CEAA58AF866012F9311516E8]
    //    RawMessage[timeStampNs=1437921600, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=1447383100, bytes=8D4D022199108D1138540D133A2B]
    //    RawMessage[timeStampNs=1472930400, bytes=8D4241A999086D0DD0840B247C82]
    //    RawMessage[timeStampNs=1499146900, bytes=8D4D2228234994B7284820323B81]
    //    RawMessage[timeStampNs=1515918300, bytes=8D39D30058C385F57F37C1B8861A]
    //    RawMessage[timeStampNs=1518447400, bytes=8D4D2228EA466864931C082073D1]
    //    RawMessage[timeStampNs=1541942000, bytes=8D4CA2BF990C4FB03018085E8C8D]
    //    RawMessage[timeStampNs=1580887900, bytes=8D4D222860B9827A4747D7A66FAA]
    //    RawMessage[timeStampNs=1581106900, bytes=8D4BCDE958B98305E57CB5879942]
    //    RawMessage[timeStampNs=1605989900, bytes=8D39D300990CE72C70049010F777]
    //    RawMessage[timeStampNs=1645019700, bytes=8D4D029F594B42EF9D7E75C0F7E5]
    //    RawMessage[timeStampNs=1646217000, bytes=8D4241A9F82300030048B81F5D33]
    //    RawMessage[timeStampNs=1648348000, bytes=8D4B17E5EA0BD89C1D7C0824B27A]
    //    RawMessage[timeStampNs=1656866000, bytes=8D44023758BF06021F11007456B4]
    //    RawMessage[timeStampNs=1687618800, bytes=8D4D029FEA1978667B5F042202CE]
    //    RawMessage[timeStampNs=1688150900, bytes=8D3C648158AF92F78DBC47B1A748]
    //    RawMessage[timeStampNs=1689976100, bytes=8D4D0221EA0DC898015E182AAE02]
    //    RawMessage[timeStampNs=1714855000, bytes=8D4CA2BF606502FEB921CE197BF9]
    //    RawMessage[timeStampNs=1722323400, bytes=8D4951CE99090B1D980808F84DB7]
    //    RawMessage[timeStampNs=1731065400, bytes=8D4952999915769CF02089DB69B1]
    //    RawMessage[timeStampNs=1761704200, bytes=8D440237EA485858013C08109653]
    //    RawMessage[timeStampNs=1762693600, bytes=8D4951CE607386A501A95EC0DE7F]
    //    RawMessage[timeStampNs=1773870400, bytes=8D495299EA447860015F8829B831]
    //    RawMessage[timeStampNs=1823092600, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=1835102700, bytes=8D4D0221581342C90B5F16AB915B]
    //    RawMessage[timeStampNs=1859535300, bytes=8D4B1A00990CD895E8480A755B34]
    //    RawMessage[timeStampNs=1872922900, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=1896411000, bytes=8D4B17E5582512ADA75565D069A5]
    //    RawMessage[timeStampNs=1905870900, bytes=8DA4F2399915C318B8048AAB1FB6]
    //    RawMessage[timeStampNs=1930690100, bytes=8D4241A9E1059700000000B7C67C]
    //    RawMessage[timeStampNs=1955871700, bytes=8DA4F23958AF8567092906082F82]
    //    RawMessage[timeStampNs=1993098600, bytes=8D3C6481EA42885C573C08357403]
    //    RawMessage[timeStampNs=2011173700, bytes=8D49529958B30662B356CC3FFCA0]
    //    RawMessage[timeStampNs=2012822900, bytes=8D4D022199108D1138540D133A2B]
    //    RawMessage[timeStampNs=2031706700, bytes=8D440237990D6D9FB8048C81C4C4]
    //    RawMessage[timeStampNs=2075921200, bytes=8D4D029F594B42EF8B7E6CCE2ECE]
    //    RawMessage[timeStampNs=2076126500, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=2076864400, bytes=8D4241A9601B62DA6D67D836061B]
    //    RawMessage[timeStampNs=2118341200, bytes=8D4D222860B985F8573FE9F76D23]
    //    RawMessage[timeStampNs=2175222800, bytes=8D4D2228F823000600487886B825]
    //    RawMessage[timeStampNs=2176455400, bytes=8D4B17E5990CAD11A8440D7C2FBE]
    //    RawMessage[timeStampNs=2181294100, bytes=8D4D22289909451F10088C706E3B]
    //    RawMessage[timeStampNs=2186271000, bytes=8D4B29645949A68E578D05384ACA]
    //    RawMessage[timeStampNs=2186476200, bytes=8D4B29649910770C105008F4F234]
    //    RawMessage[timeStampNs=2240535600, bytes=8F01024C233530F3CF6C60A19669]
    //    RawMessage[timeStampNs=2266325300, bytes=8D4952999915769CF02089DB69B1]
    //    RawMessage[timeStampNs=2283115100, bytes=8D3C648158AF92F7BBBC55059A34]
    //    RawMessage[timeStampNs=2288212800, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=2289331500, bytes=8D4B1A00583362BC1563FE29589A]
    //    RawMessage[timeStampNs=2289602000, bytes=8D4B1A00990CD895E8480A755B34]
    //    RawMessage[timeStampNs=2345132300, bytes=8D4D0221581342C9155F1C34E2B2]
    //    RawMessage[timeStampNs=2367505900, bytes=8D39CEAA58AF82E42CFF654A58C2]
    //    RawMessage[timeStampNs=2367925700, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=2386931400, bytes=8D4BCDE99915851938048B4C6DA2]
    //    RawMessage[timeStampNs=2471212800, bytes=8D49529958B302E6715F4A9FB0A3]
    //    RawMessage[timeStampNs=2473121200, bytes=8D3C6481F82300020049B832939F]
    //    RawMessage[timeStampNs=2496301600, bytes=8D4CA2BF990C4AB0501408D67300]
    //    RawMessage[timeStampNs=2516450900, bytes=8D4241A999086D0DD0840B247C82]
    //    RawMessage[timeStampNs=2558471700, bytes=8D4B17E19908EFA5A8A00C7AA410]
    //    RawMessage[timeStampNs=2567937000, bytes=8D4D222860B9827A7947F77DCDAD]
    //    RawMessage[timeStampNs=2580798000, bytes=8D39D30058C38277CB3F73D4F792]
    //    RawMessage[timeStampNs=2584585300, bytes=8D4BCDE958B98681AF734786CE14]
    //    RawMessage[timeStampNs=2597642100, bytes=8D4D022199108C1118580D1D16CC]
    //    RawMessage[timeStampNs=2619259200, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]
    //    RawMessage[timeStampNs=2662471400, bytes=8D4D029F594B42EF6F7E5F223666]
    //    RawMessage[timeStampNs=2662676700, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=2693625500, bytes=8D4952999915769CF02089DB69B1]
    //    RawMessage[timeStampNs=2698727800, bytes=8D49529923501439CF1820419C55]
    //    RawMessage[timeStampNs=2707170900, bytes=8D4D22289909451F10088C706E3B]
    //    RawMessage[timeStampNs=2729261700, bytes=8D4B1A0058336638E35B45087116]
    //    RawMessage[timeStampNs=2729532100, bytes=8D4B1A00990CD895E8480A755B34]
    //    RawMessage[timeStampNs=2789833600, bytes=8D4D2228EA466864931C082073D1]
    //    RawMessage[timeStampNs=2799304900, bytes=8D4B1A00F8210002004BB8B7E02D]
    //    RawMessage[timeStampNs=2804904800, bytes=8D4241A9EA11A898011C08B21C01]
    //    RawMessage[timeStampNs=2808517000, bytes=8D4B17E55825162ADD4D0404FD7B]
    //    RawMessage[timeStampNs=2813102200, bytes=8D3C648158AF9673C5B18C4AEC5F]
    //    RawMessage[timeStampNs=2870825200, bytes=8D39D300EA4A5867A53C089D5A75]
    //    RawMessage[timeStampNs=2883205900, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=2896592600, bytes=8D4B17E5EA0BD89C1D7C0824B27A]
    //    RawMessage[timeStampNs=2900188000, bytes=8D4D029FEA1978667B5F042202CE]
    //    RawMessage[timeStampNs=2906194400, bytes=8D49529958B302E65F5F3B65C105]
    //    RawMessage[timeStampNs=2911496600, bytes=8D4D022158134645CB5694B0537D]
    //    RawMessage[timeStampNs=2912510900, bytes=8D39CEAA58AF86608AF926B3C4A1]
    //    RawMessage[timeStampNs=2912934400, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=2935911000, bytes=8DA4F23958AF85672F28DA6B8A8F]
    //    RawMessage[timeStampNs=3011236500, bytes=8D495299EA447860015F8829B831]
    //    RawMessage[timeStampNs=3066745000, bytes=8D440237990D6D9FB8048C81C4C4]
    //    RawMessage[timeStampNs=3113140100, bytes=8D4D022199108C11185C0D2520CC]
    //    RawMessage[timeStampNs=3128442900, bytes=8D4B17E1F8210002004BB8B56D2D]
    //    RawMessage[timeStampNs=3148679700, bytes=8D4952999915779CF02089D8637F]
    //    RawMessage[timeStampNs=3150286900, bytes=8D4D222860B985F8894009F592C9]
    //    RawMessage[timeStampNs=3163623100, bytes=8D3C6545EA4A5858013C0839B8AE]
    //    RawMessage[timeStampNs=3171165200, bytes=8D4B29649910770C104C085C7034]
    //    RawMessage[timeStampNs=3179279100, bytes=8D4B1A0058335638D55B3DEF97ED]
    //    RawMessage[timeStampNs=3179549600, bytes=8D4B1A00990CD895E8440A3D0134]
    //    RawMessage[timeStampNs=3210517700, bytes=8F01024C99256F1F78048C290D2D]
    //    RawMessage[timeStampNs=3215880100, bytes=8DA4F23925101331D73820FC8E9F]
    //    RawMessage[timeStampNs=3303948100, bytes=8D4D22289909451F10088C706E3B]
    //    RawMessage[timeStampNs=3322516000, bytes=8D39CEAA58AF82E484FF5D8AD34A]
    //    RawMessage[timeStampNs=3322936000, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=3328440000, bytes=8D4B17E1E11AAC000000004DD627]
    //    RawMessage[timeStampNs=3333067700, bytes=8D3C648158AF92F80DBC6ED60A18]
    //    RawMessage[timeStampNs=3358467600, bytes=8D4B17E15857130F674183178291]
    //    RawMessage[timeStampNs=3380509700, bytes=8F01024CF8330006004AB8250493]
    //    RawMessage[timeStampNs=3413090900, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=3424260600, bytes=8D4BCDE9F8230006004BB8A0027C]
    //    RawMessage[timeStampNs=3428806700, bytes=8D49529958B312E6495F29A42CB0]
    //    RawMessage[timeStampNs=3475900700, bytes=8DA4F239F8330006004AB8123C53]
    //    RawMessage[timeStampNs=3486858100, bytes=8D4BCDE99915851938048B4C6DA2]
    //    RawMessage[timeStampNs=3495274700, bytes=8D4D0221581332C9375F2CF851D1]
    //    RawMessage[timeStampNs=3518653400, bytes=8D495299F8230002004AB8F789C5]
    //    RawMessage[timeStampNs=3532024100, bytes=8D4241A999086D0DD07C0B0F14BD]
    //    RawMessage[timeStampNs=3555524300, bytes=8D4D0221F8230002004AB8A3C16F]
    //    RawMessage[timeStampNs=3577785100, bytes=8D4D022199108C10F85C0D2B5D49]
    //    RawMessage[timeStampNs=3588444200, bytes=8D4B17E19908EEA5A8A00C79AEDE]
    //    RawMessage[timeStampNs=3663721900, bytes=8D44023758BF0601BB10BA983E9E]
    //    RawMessage[timeStampNs=3682884300, bytes=8D4D222860B9827AAF481A4C3807]
    //    RawMessage[timeStampNs=3708360900, bytes=8D4B29645949C68E758D17C588E2]
    //    RawMessage[timeStampNs=3708566200, bytes=8D4B29649910770C105008F4F234]
    //    RawMessage[timeStampNs=3719244300, bytes=8D4B1A0058335638BF5B30AAA54F]
    //    RawMessage[timeStampNs=3719514700, bytes=8D4B1A00990CD795C8440A691F07]
    //    RawMessage[timeStampNs=3727943400, bytes=8D39CEAA99144D36000489F83CF3]
    //    RawMessage[timeStampNs=3732622800, bytes=8D4951CE6073832A55B435862EAE]
    //    RawMessage[timeStampNs=3756122600, bytes=8D4CA2BF606512FE1B21C162A757]
    //    RawMessage[timeStampNs=3758671100, bytes=8D4D22289909451F10088C706E3B]
    //    RawMessage[timeStampNs=3768086500, bytes=8D3C648158AF92F82BBC7740EB0A]
    //    RawMessage[timeStampNs=3790657900, bytes=8D4BCDE9EA466867497C08E62323]
    //    RawMessage[timeStampNs=3843907900, bytes=8D49529958B312E6355F1A11D6AD]
    //    RawMessage[timeStampNs=3860772600, bytes=8D4CA2BFEA2D0866151C0809ED28]
    //    RawMessage[timeStampNs=3885921800, bytes=8DA4F23958AF81E6E92FEDD50D36]
    //    RawMessage[timeStampNs=3909284000, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]
    //    RawMessage[timeStampNs=3933091000, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=3986892300, bytes=8D4B2964EA234860015C00864171]
    //    RawMessage[timeStampNs=4011575200, bytes=8D4D022158133645EB56A467489C]
    //    RawMessage[timeStampNs=4011725100, bytes=8D440237F82300030049B823E8E4]
    //    RawMessage[timeStampNs=4038999700, bytes=8D4D2228EA466864931C082073D1]
    //    RawMessage[timeStampNs=4077709100, bytes=8D4D022199108C10F85C0D2B5D49]
    //    RawMessage[timeStampNs=4103219900, bytes=8D4B2964212024123E0820939C6F]
    //    RawMessage[timeStampNs=4144742000, bytes=8D4B17E5EA0BD89C1D7C0824B27A]
    //    RawMessage[timeStampNs=4171624600, bytes=8D4CA2BF990C41B07014088E460B]
    //    RawMessage[timeStampNs=4173289500, bytes=8D4D029F9914DF9BB824052756EC]
    //    RawMessage[timeStampNs=4175518300, bytes=8D4D0221EA0DC898015E182AAE02]
    //    RawMessage[timeStampNs=4176740300, bytes=8D4B17E5990C9F1348440D59B572]
    //    RawMessage[timeStampNs=4200509200, bytes=8D01024C99256F1F78048C99EFDD]
    //    RawMessage[timeStampNs=4201726800, bytes=8D440237EA485858013C08109653]
    //    RawMessage[timeStampNs=4211726900, bytes=8D440237990D6D9FB8088CC99EC4]
    //    RawMessage[timeStampNs=4213719800, bytes=8D4952999915779CF02089D8637F]
    //    RawMessage[timeStampNs=4220520200, bytes=8D01024C58B99639199DC1080441]
    //    RawMessage[timeStampNs=4222114100, bytes=8D4D222860B985F8BF402B408EDD]
    //    RawMessage[timeStampNs=4222824200, bytes=8D4B29645949C31313970B3F89A9]
    //    RawMessage[timeStampNs=4223029400, bytes=8D4B29649910770C104C085C7034]
    //    RawMessage[timeStampNs=4227579400, bytes=8D39CEAA58AF866102F91C1DD248]
    //    RawMessage[timeStampNs=4228002900, bytes=8D39CEAA99144E36000489FD23A1]
    //    RawMessage[timeStampNs=4229259700, bytes=8D4B1A00583352BBCF63D46240A1]
    //    RawMessage[timeStampNs=4229530200, bytes=8D4B1A00990CD795C8440A691F07]
    //    RawMessage[timeStampNs=4261388900, bytes=8D4CAC87991506AD58088C7FB79B]
    //    RawMessage[timeStampNs=4294540900, bytes=8D4D22289909451F10088C706E3B]
    //    RawMessage[timeStampNs=4368110200, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=4368912000, bytes=8D49529958B31662475676C68E5D]
    //    RawMessage[timeStampNs=4421279500, bytes=8D4BCDE958B98681F97301FE2C9D]
    //    RawMessage[timeStampNs=4424072900, bytes=8D3C654558C396BA8BB794BB4351]
    //    RawMessage[timeStampNs=4452202800, bytes=8D4B2964F8132006005AB8E3A7B3]
    //    RawMessage[timeStampNs=4475329800, bytes=8D4D022158132645F756A92CEF33]
    //    RawMessage[timeStampNs=4478263300, bytes=8D3C6481EA42885C573C08357403]
    //    RawMessage[timeStampNs=4483266200, bytes=8D3C6481E10D0100000000A919FA]
    //    RawMessage[timeStampNs=4535927800, bytes=8DA4F2399915C318B8048AAB1FB6]
    //    RawMessage[timeStampNs=4568559900, bytes=8D3C65459908CD30780490EA95F8]
    //    RawMessage[timeStampNs=4570410500, bytes=8D4BCDE99915851938048B4C6DA2]
    //    RawMessage[timeStampNs=4581047700, bytes=8D4D029F594B366B1F74E2940745]
    //    RawMessage[timeStampNs=4581253000, bytes=8D4D029F9914DF9BB824052756EC]
    //    RawMessage[timeStampNs=4582695900, bytes=8D4D022199108B10D85C0D67150A]
    //    RawMessage[timeStampNs=4590456200, bytes=8D4D2228F823000600487886B825]
    //    RawMessage[timeStampNs=4608801200, bytes=8D4B17E55823F62B174CE929B2A0]
    //    RawMessage[timeStampNs=4619251500, bytes=8D4B1A00224C16B5D2082077AB1A]
    //    RawMessage[timeStampNs=4623269100, bytes=8D4CA2BF990C41B07014088E460B]
    //    RawMessage[timeStampNs=4633873700, bytes=8D4952999915779CF01C894EA576]
    //    RawMessage[timeStampNs=4669266500, bytes=8D4B1A00583342BBC163CBEFF75F]
    //    RawMessage[timeStampNs=4669537000, bytes=8D4B1A00990CD795C8480A214507]
    //    RawMessage[timeStampNs=4672806400, bytes=8D4951CE6073832A81B44F0E093A]
    //    RawMessage[timeStampNs=4673064100, bytes=8D3C648158AF96744BB1B43010C1]
    //    RawMessage[timeStampNs=4676666100, bytes=8D4B2964E115BC000000008AC818]
    //    RawMessage[timeStampNs=4722539900, bytes=8D39CEAA58AF82E4FEFF5212B9E3]
    //    RawMessage[timeStampNs=4722963600, bytes=8D39CEAA99144E36000489FD23A1]
    //    RawMessage[timeStampNs=4730733600, bytes=8D4B29645949C68E878D2327FDF8]
    //    RawMessage[timeStampNs=4730938900, bytes=8D4B29649910770C104C085C7034]
    //    RawMessage[timeStampNs=4750361000, bytes=8D4D0221224D65F2E56360ADAA1E]
    //    RawMessage[timeStampNs=4768552300, bytes=8D4D222860B985F8D9403C5CC3DC]
    //    RawMessage[timeStampNs=4825922400, bytes=8DA4F23958AF81E70D2FC5ED906B]
    //    RawMessage[timeStampNs=4963119500, bytes=8D3C6481F82300020049B832939F]
    //    RawMessage[timeStampNs=5005920500, bytes=8DA4F239EA428864015C08A3C151]
    //    RawMessage[timeStampNs=5021779700, bytes=8D4D029F594B32EF057E29097812]
    //    RawMessage[timeStampNs=5021984900, bytes=8D4D029F9914DF9BB824052756EC]
    //    RawMessage[timeStampNs=5109255900, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]
    //    RawMessage[timeStampNs=5123076500, bytes=8D3C648158AF967469B1BD91400F]
    //    RawMessage[timeStampNs=5127926700, bytes=8D4D022199108B10B8600D3E5778]
    //    RawMessage[timeStampNs=5159521200, bytes=8D4B1A00990CD795C8480A214507]
    //    RawMessage[timeStampNs=5219108600, bytes=8D4D222860B985F8EF404AEA8446]
    //    RawMessage[timeStampNs=5232903200, bytes=8D4B17E5990C971408440D18E9E9]
    //    RawMessage[timeStampNs=5240059000, bytes=8D4CA2BF234994B5E70DE0BAE6F8]
    //    RawMessage[timeStampNs=5250520700, bytes=8D01024C58B992BC71A7F3935208]
    //    RawMessage[timeStampNs=5259942500, bytes=8D4B29645949D313279717A0E4F6]
    //    RawMessage[timeStampNs=5260147800, bytes=8D4B29649910770C104808644634]
    //    RawMessage[timeStampNs=5269207800, bytes=8D4D2228EA466864931C082073D1]
    //    RawMessage[timeStampNs=5277535800, bytes=8D39CEAA58AF866158F915C0E0E5]
    //    RawMessage[timeStampNs=5279604000, bytes=8D4B2964EA234860015C00864171]
    //    RawMessage[timeStampNs=5316792000, bytes=8D440237990D6D9FB8088CC99EC4]
    //    RawMessage[timeStampNs=5349263100, bytes=8D4B1A00F8210002004BB8B7E02D]
    //    RawMessage[timeStampNs=5374073500, bytes=8D3C654558C3934025C2ECF94AC1]
    //    RawMessage[timeStampNs=5403529100, bytes=8D4D029FEA1978667B5F042202CE]
    //    RawMessage[timeStampNs=5416696100, bytes=8D4D22289909451F10048DC7C032]
    //    RawMessage[timeStampNs=5418420800, bytes=8D4B17E15855D68A6339DBD68C70]
    //    RawMessage[timeStampNs=5422783600, bytes=8D4D0221EA0DC898015E182AAE02]
    //    RawMessage[timeStampNs=5444719600, bytes=8D4BB2795869A29ADD93D0A7BA00]
    //    RawMessage[timeStampNs=5472936100, bytes=8D4B17E55823F2AE1B55309CBF71]
    //    RawMessage[timeStampNs=5480739200, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=5494001400, bytes=8D495299EA447860015F8829B831]
    //    RawMessage[timeStampNs=5526691500, bytes=8D4CA2BF990C3CB09014082FF7FB]
    //    RawMessage[timeStampNs=5529686500, bytes=8D4D029FF8132006005AB8D115B9]
    //    RawMessage[timeStampNs=5544778400, bytes=8D4BB279990D4E17B8440735BD6E]
    //    RawMessage[timeStampNs=5607405000, bytes=8D4D022199108B10B8600D3E5778]
    //    RawMessage[timeStampNs=5619249800, bytes=8D4B1A00583342BB9F63B6070EC8]
    //    RawMessage[timeStampNs=5619520400, bytes=8D4B1A00990CD795C8440A691F07]
    //    RawMessage[timeStampNs=5648568500, bytes=8D3C6545EA4A5858013C0839B8AE]
    //    RawMessage[timeStampNs=5684907400, bytes=8D4CA2BF606512FD8321B545D829]
    //    RawMessage[timeStampNs=5688356200, bytes=8D3C648158AF967495B1CACD05E9]
    //    RawMessage[timeStampNs=5693093200, bytes=8D3C6481EA42885C573C08357403]
    //    RawMessage[timeStampNs=5705933900, bytes=8DA4F2399915C318B8048AAB1FB6]
    //    RawMessage[timeStampNs=5710204500, bytes=8D4CA2BFE102BF00000000A22DDD]
    //    RawMessage[timeStampNs=5715422700, bytes=8D4B29645949D68E9B8D2F93AE5E]
    //    RawMessage[timeStampNs=5715627900, bytes=8D4B29649910780C10480875242E]
    //    RawMessage[timeStampNs=5722728800, bytes=8D4951CE99090B1D980408B017B7]
    //    RawMessage[timeStampNs=5723088700, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=5736587400, bytes=8D49529958B312E5DB5ED200F24C]
    //    RawMessage[timeStampNs=5742208300, bytes=8D4D222860B9827B17485C78FC85]
    //    RawMessage[timeStampNs=5765929900, bytes=8DA4F23958AF85679D2860045546]
    //    RawMessage[timeStampNs=5847966700, bytes=8D39CEAA99144E36000489FD23A1]
    //    RawMessage[timeStampNs=5947403200, bytes=8D4D22289909451F10048C38343B]
    //    RawMessage[timeStampNs=5998271000, bytes=8D4D029F594B32EED97E13C783BD]
    //    RawMessage[timeStampNs=5998476400, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=6025450100, bytes=8D4D0221581316461F56BEB50FC4]
    //    RawMessage[timeStampNs=6049248700, bytes=8D4B1A00583332BB9163AD296B40]
    //    RawMessage[timeStampNs=6060510700, bytes=8F01024CEA466864015C0866F082]
    //    RawMessage[timeStampNs=6103072700, bytes=8D3C648158AF8674BBB1D505CE3E]
    //    RawMessage[timeStampNs=6117960100, bytes=8D4CA2BF990C37B09018087AE4D9]
    //    RawMessage[timeStampNs=6163110900, bytes=8D4D022199108A10B8600CC2A9BF]
    //    RawMessage[timeStampNs=6242739900, bytes=8D4CA2BF606512FD5921B38D4E61]
    //    RawMessage[timeStampNs=6248882000, bytes=8D4BCDE9EA466867497C08E62323]
    //    RawMessage[timeStampNs=6252823600, bytes=8D4B29645949E3133B97228B4B9D]
    //    RawMessage[timeStampNs=6286746200, bytes=8D440237990D6D9FB8088CC99EC4]
    //    RawMessage[timeStampNs=6288172900, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=6297064200, bytes=8D4B17E5990C8F14E8400D2DA5AF]
    //    RawMessage[timeStampNs=6310512200, bytes=8F01024C58B99639819D7618F388]
    //    RawMessage[timeStampNs=6316314200, bytes=8D4BCDE958B986824572B9A1BEB4]
    //    RawMessage[timeStampNs=6336628600, bytes=8D4B1A1EEA3CA865733C082A79CD]
    //    RawMessage[timeStampNs=6339239700, bytes=8D4B1A00EA0DC89E8F7C0857D5F5]
    //    RawMessage[timeStampNs=6375464700, bytes=8D4CA2BFEA2D0866151C0809ED28]
    //    RawMessage[timeStampNs=6380505800, bytes=8F01024C99256F1F58048C6C7104]
    //    RawMessage[timeStampNs=6408458600, bytes=8D4B17E15855C68A2739F2A5CC13]
    //    RawMessage[timeStampNs=6432548700, bytes=8D39CEAA58AF8661BEF90C36FD08]
    //    RawMessage[timeStampNs=6432972300, bytes=8D39CEAA99144E36000489FD23A1]
    //    RawMessage[timeStampNs=6495446500, bytes=8D4D2228EA466864931C082073D1]
    //    RawMessage[timeStampNs=6503498300, bytes=8D4D0221581306462B56C4280466]
    //    RawMessage[timeStampNs=6507943600, bytes=8D4BCDE99915851938048B4C6DA2]
    //    RawMessage[timeStampNs=6532434900, bytes=8D4D2228234994B7284820323B81]
    //    RawMessage[timeStampNs=6540584200, bytes=8D4D029F594B266AC774B7A0E396]
    //    RawMessage[timeStampNs=6540789400, bytes=8D4D029F9914E09BB828052F9BD6]
    //    RawMessage[timeStampNs=6565584200, bytes=8D4D022199108A1098600C87D596]
    //    RawMessage[timeStampNs=6569525800, bytes=8D4B1A00990CD695A8440AA591B2]
    //    RawMessage[timeStampNs=6586640700, bytes=8D4B1A1E5899B24BD999322D0F24]
    //    RawMessage[timeStampNs=6609334700, bytes=8D4CA2BF990C37B09018087AE4D9]
    //    RawMessage[timeStampNs=6638424800, bytes=8D4B17E19908EBA5E8A00CFC777A]
    //    RawMessage[timeStampNs=6649117100, bytes=8D4B17E55823E2AE455520B91485]
    //    RawMessage[timeStampNs=6653130500, bytes=8D3C648158AF92F907BCB973489D]
    //    RawMessage[timeStampNs=6658549900, bytes=8D4D029FEA1978667B5F042202CE]
    //    RawMessage[timeStampNs=6703087200, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=6709405000, bytes=8D4D0221EA0DC898015E182AAE02]
    //    RawMessage[timeStampNs=6711750100, bytes=8D44023758BF028353171EA5977F]
    //    RawMessage[timeStampNs=6733320500, bytes=8D4CA2BF606512FD3321B0C82882]
    //    RawMessage[timeStampNs=6734485300, bytes=8D440237EA485858013C08109653]
    //    RawMessage[timeStampNs=6739433700, bytes=8D4B29645949E68EAD8D3A89B626]
    //    RawMessage[timeStampNs=6739638900, bytes=8D4B29649910780C10480875242E]
    //    RawMessage[timeStampNs=6796662500, bytes=8D4B1A1E99091DA45080840405CC]
    //    RawMessage[timeStampNs=6857664800, bytes=8D39CEAA58AF8661E0F909DCD6FB]
    //    RawMessage[timeStampNs=6858084900, bytes=8D39CEAA99144E36000489FD23A1]
    //    RawMessage[timeStampNs=6881606700, bytes=8D49529958B32661D9561F63A10E]
    //    RawMessage[timeStampNs=6883614600, bytes=8D4B2964F8132006005AB8E3A7B3]
    //    RawMessage[timeStampNs=6938109300, bytes=8D3C6481EA42885C573C08357403]
    //    RawMessage[timeStampNs=6946551100, bytes=8D4D22289909451F18048C569633]
    //    RawMessage[timeStampNs=6965499200, bytes=8D4D022199108A10985C0C11139F]
    //    RawMessage[timeStampNs=7009169300, bytes=8D4D029F21286071DD3820D76F68]
    //    RawMessage[timeStampNs=7013032200, bytes=8D4D222860B9827B57488509E5CA]
    //    RawMessage[timeStampNs=7019252900, bytes=8D4B1A00583332BB6F63986D195F]
    //    RawMessage[timeStampNs=7019523400, bytes=8D4B1A00990CD695A8440AA591B2]
    //    RawMessage[timeStampNs=7059959800, bytes=8D4D029F594B22EEAB7DFDE94165]
    //    RawMessage[timeStampNs=7060165000, bytes=8D4D029F9914E09BB8240567C1D6]
    //    RawMessage[timeStampNs=7060665700, bytes=8D4D0221581302C9975F5E046266]
    //    RawMessage[timeStampNs=7157583600, bytes=8D39CEAA205161B7CF0DE02A2AAF]
    //    RawMessage[timeStampNs=7193198000, bytes=8D4B17E5234D74B6308820462627]
    //    RawMessage[timeStampNs=7201532100, bytes=8D4952999915779CF01C894EA576]
    //    RawMessage[timeStampNs=7203089700, bytes=8D4BCDE9234D84F51C88209BFD58]
    //    RawMessage[timeStampNs=7208021100, bytes=8D4B29645949E3134D972EB53A2A]
    //    RawMessage[timeStampNs=7208226400, bytes=8D4B29649910780C304808305807]
    //    RawMessage[timeStampNs=7228828000, bytes=8D4BCDE958B98682697297BCA460]
    //    RawMessage[timeStampNs=7253088900, bytes=8D3C64819908E62EF8048B6231B9]
    //    RawMessage[timeStampNs=7257254700, bytes=8D4B17E5990C8615A8440DAFCA10]
    //    RawMessage[timeStampNs=7306755600, bytes=8D4402372314A576CC8060E89CB9]
    //    RawMessage[timeStampNs=7363116900, bytes=8D3C6481F82300020049B832939F]
    //    RawMessage[timeStampNs=7380502000, bytes=8F01024C99256F1F58048C6C7104]
    //    RawMessage[timeStampNs=7382974700, bytes=8D4D02219910891098540D9B94C4]
    //    RawMessage[timeStampNs=7400051800, bytes=8D4D22289909451F18048C569633]
    //    RawMessage[timeStampNs=7403217000, bytes=8D39CEAA99144E36000489FD23A1]
    //    RawMessage[timeStampNs=7440752300, bytes=8D4D222860B985F95D408F53A49C]
    //    RawMessage[timeStampNs=7463177400, bytes=8D4D02215811F2C9A15F63B25366]
    //    RawMessage[timeStampNs=7465266700, bytes=8D4B17E5F8210002004BB8B1F1AC]
}