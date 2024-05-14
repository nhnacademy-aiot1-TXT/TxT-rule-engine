package com.nhnacademy.aiot.ruleengine.constants;

public final class Constants {

    public static final String RULE_ENGINE = "RuleEngine";
    public static final String RULE_ENGINE_STOP_MESSAGE = "룰엔진 서버가 종료되었습니다.";
    public static final String INTRUSION = "intrusion";

    private Constants() {
        throw new UnsupportedOperationException("유틸 클래스입니다.");
    }

    public static final String AIR_CONDITIONER_CHANNEL = "airConditionerChannel";
    public static final String TXT_MQTT = "tcp://133.186.229.200:1883";
    public static final String ACADEMY_MQTT = "tcp://133.186.153.19:1883";
    public static final String OCCUPIED = "occupied";
    public static final String AIRCLEANER = "aircleaner";
    public static final Long AIRCLEANER_DEVICE_ID = 1L;
    public static final Long AIRCLEANER_SENSOR_ID = 1L;
    public static final Long AIRCONDITIONER_DEVICE_ID = 2L;
    public static final Long AIRCONDITIONER_SENSOR_ID = 6L;
    public static final String AIR_CLEANER_CHANNEL = "airCleanerChannel";
    public static final String VOC = "voc";
    public static final String AIRCONDITIONER = "airconditioner";
    public static final String AUTO_AIRCONDITIONER = "auto_airconditioner";
    public static final String TIMER = "_timer";
    public static final String STATUS = "_status";
    public static final String VACANT = "vacant";
    public static final String OCCUPANCY = "occupancy";
    public static final String TEMPERATURE = "temperature";
    public static final String LIGHT = "light";
    public static final String DEVICE_POWER_STATUS = "device_power_status";
    public static final String AUTO_MODE = "auto_mode";
    public static final String OCCUPANCY_CHANNEL = "occupancyChannel";
    public static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";
    public static final String CLASS_A = "class_a";
    public static final String PREVIOUS_OUTDOOR = "previous_outdoor";
    public static final String OUTDOOR = "outdoor";
    public static final String OUTDOOR_TEMPERATURE = "outdoorTemperature";
    public static final String OUTDOOR_HUMIDITY = "outdoorHumidity";
    public static final String INDOOR_TEMPERATURE = "indoorTemperature";
    public static final String INDOOR_HUMIDITY = "indoorHumidity";
    public static final String TOTAL_PEOPLE_COUNT = "totalPeopleCount";
    public static final String TIME = "time";
    public static final String HUMIDITY = "humidity";
    public static final String DEVICE = "device";
    public static final String PLACE = "place";
    public static final String TOPIC = "topic";
    public static final String VALUE = "value";
    public static final String INTRUSION_CHANNEL = "intrusionChannel";
    public static final int ONE_MINUTE_THIRTY_SECONDS = 90000;
    public static final int ONE_MINUTE = 60000;
    public static final int TEN_MINUTES = 600000;
    public static final String OCCUPANCY_LABEL = "_occupancy";
    public static final String INFLUX_SAVE_ERROR_MESSAGE = "[ERROR] InfluxDB 서버에 저장하는 과정에서 문제가 발생했습니다.";
    public static final String INFLUX_DB = "influxDB";
    public static final String BATTERY = "battery";
    public static final String LOW = "low";
    public static final String CRITICAL = "critical";
    public static final String BATTERY_LEVEL_CHANNEL = "batteryLevelChannel";
    public static final int PLACE_INDEX = 6;
    public static final int DEVICE_INDEX = 8;
    public static final int LOW_LEVEL = 20;
    public static final int CRITICAL_LEVEL = 10;
}

