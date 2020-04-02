package com.ust.smartph;

import android.support.v7.app.AppCompatActivity;

@Deprecated
public class TimetableActivity extends AppCompatActivity {
//    public static final int REQUEST_ADD = 1;
//    public static final int REQUEST_EDIT = 2;
//
//    @BindView(R.id.timetable_thrsun)
//    TimetableView timetable;
//
//
//    String fileName;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_timetable);
//        ButterKnife.bind(TimetableActivity.this);
//        int now=DayOfWeek.from(LocalDate.now()).getValue();
//        //TODO 1: Load default timetable
//        initView();
//    }
//
//    private void initView() {
//        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
//            @Override
//            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
//                Log.d("", String.format("idx is %d", idx));
//                Intent i = new Intent(TimetableActivity.this, EditActivity.class);
//                i.putExtra("mode", REQUEST_EDIT);
//                i.putExtra("idx", idx);
//                i.putExtra("schedules", schedules);
//                startActivityForResult(i, REQUEST_EDIT);
//            }
//        });
//    }
//
//    @Override
//    @OnClick({R.id.add_btn, R.id.clear_btn, R.id.save_btn, R.id.load_btn, R.id.match_btn})
//    public void onClick(View v) {
//        //very disgusting method to solve getting data from async view
//        switch (v.getId()) {
//            case R.id.add_btn:
//                Intent i = new Intent(this, EditActivity.class);
//                i.putExtra("mode", REQUEST_ADD);
//                startActivityForResult(i, REQUEST_ADD);
//                break;
//            case R.id.clear_btn:
//                timetable.removeAll();
//                break;
//            default:
//                showSelectOption(v.getId());
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        switch (requestCode) {
//            case REQUEST_ADD:
//                if (resultCode == EditActivity.RESULT_OK_ADD) {
//                    ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
//                    timetable.add(item);
//                    System.out.println(timetable.getAllSchedulesInStickers().size());
//                }
//                break;
//            case REQUEST_EDIT:
//                /** Edit -> Submit */
//                if (resultCode == EditActivity.RESULT_OK_EDIT) {
//                    int idx = data.getIntExtra("idx", -1);
//                    ArrayList<Schedule> item = (ArrayList<Schedule>) data.getSerializableExtra("schedules");
//                    timetable.edit(idx, item);
//                }
//                /** Edit -> Delete */
//                else if (resultCode == EditActivity.RESULT_OK_DELETE) {
//                    int idx = data.getIntExtra("idx", -1);
//                    timetable.remove(idx);
//                }
//                break;
//        }
//    }
//
//    private void showSelectOption(int buttonID) {
//        String[] saveOptions = {"test_1", "test_2", "test_3", "timetable_demo"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("SharedPref names");
//        builder.setItems(saveOptions, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which != -1) {
//                    TimetableActivity.this.fileName = saveOptions[which];
//                    switch (buttonID) {
//                        case R.id.save_btn:
//                            saveByPreference(timetable.createSaveData());
//                            break;
//                        case R.id.load_btn:
//                            loadSavedData();
//                            break;
//                        case R.id.match_btn:
//                            //TODO 2: use a new activity to display timetable
//                            matchTimeTable();
//                        default:
//                            break;
//                    }
//                } else {
//                    Log.e("", "which is -1!");
//                }
//            }
//        });
//        builder.show();
//    }
//
//    // vanilla timetable matching function
//    private void matchTimeTable() {
//        ArrayList<Schedule> now = new ArrayList<>(timetable.getAllSchedulesInStickers());
//        now.forEach(e-> Log.d("",String.format("day is %d start hr %d",e.getDay(),e.getStartTime().getHour())));
//        timetable.removeAll();
//        TimetableActivity.this.loadSavedData();
//        ArrayList<Schedule> other = new ArrayList<>(timetable.getAllSchedulesInStickers());
//        timetable.removeAll();
//        timetable.add(getFreeTime(now));
//    }
//
//    /**
//     * save timetableView's data to SharedPreferences in json format
//     */
//    private void saveByPreference(String data) {
//        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = mPref.edit();
//        editor.putString(this.fileName, data);
//        Log.d("json data", data);
//        editor.apply();
//        Toast.makeText(this, "saved!", Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * get json data from SharedPreferences and then restore the timetable
//     */
//    private void loadSavedData() {
//        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
//        String savedData = mPref.getString(this.fileName, "");
//        if (savedData == null || savedData.equals("")) {
//            Toast.makeText(this, "wrong", Toast.LENGTH_LONG).show();
//            return;
//        }
//        timetable.load(savedData);
//        Toast.makeText(this, "loaded!", Toast.LENGTH_SHORT).show();
//    }
//
//    private ArrayList<Schedule> getCrashTime(ArrayList<Schedule> t1, ArrayList<Schedule> t2) {
//        ArrayList<Schedule> sameTime = new ArrayList<>();
//        for (Schedule s : t1) {
//            sameTime.addAll(t2.stream().filter(e ->
//                    e.getDay() == s.getDay() &&
//                            isTimeIntercept(s, e)
//            ).collect(Collectors.toList()));
//        }
//        return sameTime;
//    }
//
//    private ArrayList<Schedule> getFreeTime(ArrayList<Schedule> now) {
//        ArrayList<Schedule> result = new ArrayList<>();
//        for (int i = 0; i < 7; i++) {
//            final int today = i;
//            Time time = new Time(9, 0);
//            ArrayList<Schedule> sameDay = new ArrayList<>(
//                    now.stream()
//                            .filter(e -> e.getDay() == today)
//                            .collect(Collectors.toList()));
//            for (int j = 0; j < sameDay.size(); j++) {
//                Time end = sameDay.get(j).getStartTime();
//                Schedule free = getFreeSchedule(i,time,end);
//                time = sameDay.get(j).getEndTime();
//                result.add(free);
//            }
//            if (result.size() > 0) {
//                Time lastItem=result.get(result.size()-1).getEndTime();
//                if(lastItem.getHour()==6 &&lastItem.getMinute()==59){
//                    continue;
//                }
//                Schedule free = getFreeSchedule(i,time,new Time(18,59));
//                result.add(free);
//            }
//            else{
//                Schedule free = getFreeSchedule(i,time,new Time(18,59));
//                result.add(free);
//            }
//
//        }
//        return result;
//    }
//
//    private Schedule getFreeSchedule(int day,Time begin, Time end){
//        Schedule free = new Schedule();
//        free.setDay(day);
//        free.setClassTitle("Free");
//        free.setStartTime(begin);
//        free.setEndTime(end);
//        return free;
//    }
//
//    private boolean isTimeIntercept(Schedule t1, Schedule t2) {
//        int startHR = Math.min(t1.getStartTime().getHour(), t2.getStartTime().getHour());
//        Schedule early = t1.getStartTime().getHour() == startHR ? t1 : t2;
//        Schedule late = early == t1 ? t2 : t1;
//        if (early.getEndTime().getHour() >= late.getStartTime().getHour()) {
//            return early.getEndTime().getMinute() < late.getStartTime().getMinute();
//        }
//        return false;
//    }
}
