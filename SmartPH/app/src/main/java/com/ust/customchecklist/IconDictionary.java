package com.ust.customchecklist;

import com.ust.smartph.R;

import java.util.HashMap;
import java.util.Map;

public class IconDictionary {

    private static HashMap<Integer,Integer> mapping;

    static {
        mapping=new HashMap<>();
        mapping.put(R.id.default_icon,R.drawable.color_splotch_selector);
        mapping.put(R.id.workout_icon,R.drawable.ic_fitness_center_white_24dp);
        mapping.put(R.id.study_icon,R.drawable.ic_school_white_24dp);
        mapping.put(R.id.shopping_icon,R.drawable.ic_local_grocery_store_white_24dp);
        mapping.put(R.id.birthday_icon,R.drawable.ic_cake_white_24dp);
        mapping.put(R.id.baby_icon,R.drawable.ic_child_friendly_white_24dp);
        mapping.put(R.id.work_icon,R.drawable.ic_credit_card_white_24dp);
        mapping.put(R.id.airplane_icon,R.drawable.ic_flight_white_24dp);
        mapping.put(R.id.gas_icon,R.drawable.ic_local_gas_station_white_24dp);
        mapping.put(R.id.mail_icon,R.drawable.ic_mail_white_24dp);
        mapping.put(R.id.people_icon,R.drawable.ic_people_white_24dp);
        mapping.put(R.id.eat_icon,R.drawable.ic_restaurant_menu_white_24dp);
        for(Map.Entry e:mapping.entrySet()){
            System.out.println(e.getKey());
        }
    }

    public static Integer getIcon(int key){
        if(mapping.containsKey(key)){
            return mapping.get(key);
        }
        return -1;
    }

}
