package com.example.medical_emergency_handling;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FirstAidActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirstAidAdapter adapter;
    private SearchView searchView;
    private List<FirstAidItem> firstAidItems;
    private Toolbar toolbarFirstAid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_aid);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewFirstAid);
        searchView = findViewById(R.id.searchView);
        toolbarFirstAid = findViewById(R.id.toolbarFirstAid);

        setSupportActionBar(toolbarFirstAid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("First Aid");
        toolbarFirstAid.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize data
        initializeFirstAidData();

        // Setup adapter
        adapter = new FirstAidAdapter(firstAidItems);
        recyclerView.setAdapter(adapter);

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    private void initializeFirstAidData() {
        firstAidItems = new ArrayList<>();

        firstAidItems.add(new FirstAidItem(
                "Heart Attack",
                R.drawable.ic_heart_attack,
                "Recognize and respond to heart attack symptoms",
                new String[]{
                        "1. Call emergency services (112) immediately",
                        "2. Help the person sit down and stay calm",
                        "3. Loosen any tight clothing",
                        "4. Give aspirin if prescribed and available",
                        "5. Monitor breathing and consciousness",
                        "6. Be prepared to perform CPR if needed",
                        "7. Keep the person still and comfortable"
                },
                new String[]{
                        "• Chest pain or pressure",
                        "• Shortness of breath",
                        "• Pain in arms, neck, jaw, or back",
                        "• Cold sweat and nausea",
                        "• Lightheadedness"
                },
                R.drawable.img_heart_attack,
                "heart_attack_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Burns",
                R.drawable.ic_burn,
                "Immediate treatment for burns",
                new String[]{
                        "1. Cool the burn under cold running water (20 minutes)",
                        "2. Remove jewelry and tight items",
                        "3. Cover with sterile gauze or clean cloth",
                        "4. Don't break blisters",
                        "5. Apply burn gel or cream if available",
                        "6. Take pain relievers if needed",
                        "7. Seek medical attention for serious burns"
                },
                new String[]{
                        "• Redness and swelling",
                        "• Blistering",
                        "• Severe pain",
                        "• White or charred skin",
                        "• Size larger than palm"
                },
                R.drawable.img_burns,
                "burns_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Choking",
                R.drawable.ic_choking,
                "Steps to help a choking victim",
                new String[]{
                        "1. Ask the person if they are choking and can speak",
                        "2. If they can't speak, give 5 back blows between the shoulder blades",
                        "3. If that doesn't work, give 5 abdominal thrusts (Heimlich maneuver)",
                        "4. Alternate back blows and abdominal thrusts until the object is dislodged"
                },
                new String[]{
                        "• Unable to speak or cough",
                        "• Struggling to breathe",
                        "• Face turning red or blue"
                },
                R.drawable.img_choking,
                "choking_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Severe Bleeding",
                R.drawable.ic_bleeding,
                "Manage severe bleeding emergencies",
                new String[]{
                        "1. Call 112 immediately",
                        "2. Apply firm, direct pressure to the bleeding site",
                        "3. Use a clean cloth, bandage, or your hands",
                        "4. Elevate the injured body part if possible",
                        "5. Do not remove any embedded objects",
                        "6. If bleeding persists, apply additional pressure"
                },
                new String[]{
                        "• Spurting or gushing blood",
                        "• Blood soaking through bandages",
                        "• Amputation of a body part"
                },
                R.drawable.img_bleeding,
                "severe_bleeding_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Seizure",
                R.drawable.ic_seizure,
                "Provide first aid during a seizure",
                new String[]{
                        "1. Stay calm and time the seizure",
                        "2. Clear the area of any hard or sharp objects",
                        "3. Gently roll the person onto their side",
                        "4. Do not put anything in their mouth",
                        "5. Do not try to hold them down",
                        "6. Stay with them until they are fully awake"
                },
                new String[]{
                        "• Convulsions",
                        "• Uncontrolled muscle spasms",
                        "• Loss of consciousness"
                },
                R.drawable.img_seizure,
                "seizure_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Stroke",
                R.drawable.ic_stroke,
                "Recognize and respond to stroke symptoms using FAST method",
                new String[]{
                        "1. Call emergency services (112) immediately",
                        "2. Note the time symptoms started",
                        "3. Keep the person still and comfortable",
                        "4. Do not give them anything to eat or drink",
                        "5. Monitor breathing and consciousness",
                        "6. Collect their medications for paramedics",
                        "7. Reassure them while waiting for help"
                },
                new String[]{
                        "• Face drooping on one side",
                        "• Arm weakness or numbness",
                        "• Speech difficulty or slurred",
                        "• Time to call emergency - every minute counts",
                        "• Sudden confusion or headache",
                        "• Vision problems"
                },
                R.drawable.img_stroke,
                "stroke_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Allergic Reaction",
                R.drawable.ic_allergy,
                "Handle severe allergic reactions (anaphylaxis)",
                new String[]{
                        "1. Call emergency services (112)",
                        "2. Help them use their epinephrine auto-injector if available",
                        "3. Keep them calm and lying flat",
                        "4. Raise their legs if possible",
                        "5. Monitor breathing and pulse",
                        "6. Be prepared to perform CPR",
                        "7. Keep them warm"
                },
                new String[]{
                        "• Difficulty breathing",
                        "• Swelling of face/throat",
                        "• Severe hives or rash",
                        "• Rapid weak pulse",
                        "• Dizziness or fainting"
                },
                R.drawable.img_allergy,
                "allergy_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Heat Exhaustion",
                R.drawable.ic_heat_exhaustion,
                "Treat heat exhaustion before it becomes heat stroke",
                new String[]{
                        "1. Move to a cool, shaded area",
                        "2. Remove excess clothing",
                        "3. Apply cool, wet cloths to body",
                        "4. Fan the person",
                        "5. Give small sips of water if alert",
                        "6. Seek medical help if symptoms worsen",
                        "7. Monitor body temperature"
                },
                new String[]{
                        "• Heavy sweating",
                        "• Cool, pale, clammy skin",
                        "• Muscle cramps",
                        "• Dizziness or headache",
                        "• Nausea or vomiting"
                },
                R.drawable.img_heat_exhaustion,
                "heat_exhaustion_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Fracture",
                R.drawable.ic_fracture,
                "Provide initial care for suspected fractures",
                new String[]{
                        "1. Call emergency services for serious fractures",
                        "2. Keep the injured area still",
                        "3. Apply ice wrapped in cloth to reduce swelling",
                        "4. Do not attempt to realign the bone",
                        "5. Support the injury in comfortable position",
                        "6. Check circulation beyond the injury",
                        "7. Treat for shock if necessary"
                },
                new String[]{
                        "• Pain and tenderness",
                        "• Swelling and bruising",
                        "• Deformity or abnormal position",
                        "• Difficulty moving the area",
                        "• Exposed bone in compound fractures"
                },
                R.drawable.img_fracture,
                "fracture_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Poisoning",
                R.drawable.ic_poisoning,
                "Handle suspected poisoning cases",
                new String[]{
                        "1. Call poison control center immediately",
                        "2. Do not induce vomiting unless instructed",
                        "3. Save the poison container/plant for identification",
                        "4. Follow poison control's instructions exactly",
                        "5. Monitor vital signs",
                        "6. Keep the person comfortable",
                        "7. Note symptoms for medical personnel"
                },
                new String[]{
                        "• Nausea or vomiting",
                        "• Difficulty breathing",
                        "• Burns around mouth",
                        "• Confusion or drowsiness",
                        "• Unusual odors on breath"
                },
                R.drawable.img_poisoning,
                "poisoning_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Diabetic Emergency",
                R.drawable.ic_diabetes,
                "Respond to diabetic emergencies (high or low blood sugar)",
                new String[]{
                        "1. If conscious and blood sugar is low, give sugar",
                        "2. Use glucose tablets, juice, or regular soda",
                        "3. Wait 15 minutes and check for improvement",
                        "4. If no improvement, call emergency services",
                        "5. If unconscious, call 112 immediately",
                        "6. Place in recovery position if unconscious",
                        "7. Monitor breathing and consciousness"
                },
                new String[]{
                        "• Confusion or irritability",
                        "• Sweating with cold, clammy skin",
                        "• Weakness or drowsiness",
                        "• Rapid heartbeat",
                        "• Loss of consciousness"
                },
                R.drawable.img_diabetes,
                "diabetic_emergency_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Hypothermia",
                R.drawable.ic_hypothermia,
                "Handle cases of dangerous body temperature drop",
                new String[]{
                        "1. Call emergency services (112) immediately",
                        "2. Move person to warm, dry area and remove wet clothing",
                        "3. Wrap in warm blankets, focusing on head and torso",
                        "4. Give warm sweet drinks if fully conscious",
                        "5. Monitor breathing and consciousness",
                        "6. Use warm compresses on chest and neck only",
                        "7. Do NOT use direct heat or massage limbs"
                },
                new String[]{
                        "• Intense shivering or stopped shivering",
                        "• Confusion or drowsiness",
                        "• Slurred speech",
                        "• Weak pulse",
                        "• Bluish skin color"
                },
                R.drawable.img_hypothermia,
                "hypothermia_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Spinal Injury",
                R.drawable.ic_spine,
                "Safely handle suspected spinal injuries",
                new String[]{
                        "1. Call emergency services immediately",
                        "2. Do NOT move the person unless in immediate danger",
                        "3. Support head and neck in current position",
                        "4. Ask them to remain still",
                        "5. Monitor breathing and consciousness",
                        "6. Keep them warm with blankets",
                        "7. Note any tingling or loss of sensation"
                },
                new String[]{
                        "• Neck or back pain",
                        "• Numbness or paralysis",
                        "• Loss of bladder/bowel control",
                        "• Twisted neck or back",
                        "• Difficulty breathing"
                },
                R.drawable.img_spine,
                "spinal_injury_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Drowning",
                R.drawable.ic_drowning,
                "Respond to drowning emergencies",
                new String[]{
                        "1. Ensure scene safety and call for help",
                        "2. Remove person from water if safe to do so",
                        "3. Check breathing and start CPR if needed",
                        "4. Place in recovery position if breathing",
                        "5. Remove wet clothes and keep warm",
                        "6. Get emergency medical care even if seems recovered",
                        "7. Monitor for secondary drowning symptoms"
                },
                new String[]{
                        "• Difficulty breathing",
                        "• Coughing up water",
                        "• Confusion or unconsciousness",
                        "• Blue skin color",
                        "• No breathing"
                },
                R.drawable.img_drowning,
                "drowning_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Eye Injury",
                R.drawable.ic_eye,
                "Handle common eye injuries and emergencies",
                new String[]{
                        "1. Do NOT rub or apply pressure to eye",
                        "2. For chemicals, flush with water for 15 minutes",
                        "3. For objects, leave them in place",
                        "4. Cover both eyes to prevent movement",
                        "5. Seek immediate medical attention",
                        "6. Keep person calm and still",
                        "7. Document what caused the injury"
                },
                new String[]{
                        "• Severe pain or redness",
                        "• Vision changes or loss",
                        "• Embedded object",
                        "• Chemical exposure",
                        "• Light sensitivity"
                },
                R.drawable.img_eye,
                "eye_injury_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Snake Bite",
                R.drawable.ic_snake_bite,
                "Handle snake bite emergencies",
                new String[]{
                        "1. Call emergency services immediately",
                        "2. Keep victim calm and still",
                        "3. Remove constricting items (jewelry)",
                        "4. Keep affected area below heart level",
                        "5. Clean wound with soap and water",
                        "6. Do NOT apply tourniquet or ice",
                        "7. Try to remember snake's appearance"
                },
                new String[]{
                        "• Puncture marks",
                        "• Severe pain and swelling",
                        "• Nausea and vomiting",
                        "• Difficulty breathing",
                        "• Blurred vision"
                },
                R.drawable.img_snake_bite,
                "snake_bite_guide"
        ));

        firstAidItems.add(new FirstAidItem(
                "Electric Shock",
                R.drawable.ic_electric_shock,
                "Respond to electrical injuries",
                new String[]{
                        "1. Do NOT touch person until power source is off",
                        "2. Call emergency services immediately",
                        "3. Check breathing and start CPR if needed",
                        "4. Look for entry and exit wounds",
                        "5. Cover burns with clean, dry dressing",
                        "6. Monitor vital signs",
                        "7. Treat for shock if necessary"
                },
                new String[]{
                        "• Burns at contact points",
                        "• Muscle pain and contractions",
                        "• Difficulty breathing",
                        "• Seizures",
                        "• Irregular heartbeat"
                },
                R.drawable.img_electric_shock,
                "electric_shock_guide"
        ));

        // Add more first aid items here...
    }
}