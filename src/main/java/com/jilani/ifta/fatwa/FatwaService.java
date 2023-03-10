package com.jilani.ifta.fatwa;

import com.jilani.ifta.notifications.NotificationService;
import com.jilani.ifta.ui.Button;
import com.jilani.ifta.ui.Modal;
import com.jilani.ifta.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//TODO: update topic and category counters accordingly when fatwa deleted.

@Service
public class FatwaService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FatwaRepository fatwaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean canApprove(long fatwaId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //SecurityContextHolderAwareRequestWrapper.isUserInRole(String role)
        Fatwa fatwa = fatwaRepository.getById(fatwaId);
        return hasRole("MAINMUFTI") && fatwa.isAnswered() && !fatwa.isApproved();
    }

    public boolean canWriteAnswer(Fatwa fatwa, User currentUser) {

        if(hasRole("MAINMUFTI"))
            return true;

        if(fatwa.getMufti() == null && hasRole("MUFTI"))
            return true;

        if(fatwa.getMufti().getUsername().equals(currentUser.getUsername()))
            return true;

        return false;
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth.isAuthenticated()
                &&
                (auth.getAuthorities().stream().anyMatch(it -> it.getAuthority().equals(role)))
        );
    }

    public boolean isUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth.isAuthenticated()
                &&
                !(auth.getAuthorities().stream().anyMatch(it -> it.getAuthority().equals("MUFTI")))
        );
    }

    public boolean hasRole(User user, String role) {
        return (user.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(role)));
    }

    public boolean hasRole(long userId, String role) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return (optionalUser.isPresent() && optionalUser.get().getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(role)));
    }

    public User getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context != null) {
            Authentication auth = context.getAuthentication();
            if(auth.isAuthenticated()) {
                Object principal = auth.getPrincipal();
                if(principal instanceof UserDetails) {
                    User user = ((CustomUserDetails) principal).getUser();
                    return user;
                }
            }
        }
        return null;
    }

    public void raiseQuestion(String title, String question, User asker, Topic topic) {
        Fatwa fatwa = new Fatwa();
        fatwa.setTopic(topic);
        fatwa.setTitle(title);
        fatwa.setQuestion(question);
        fatwa.setAsker(asker);
        fatwa.setAskedOn(new Date());
        fatwa.setAnswered(false);
        fatwa.setApproved(false);
        fatwa.setViewCount(0l);
        fatwaRepository.save(fatwa);
        System.out.println("New Question raised: " + title);

        topic.incrementCounterBy(1);
        Category category = topic.getCategory();
        category.incrementCounterBy(1);

        topicRepository.save(topic);
        categoryRepository.save(category);

        fatwa = fatwaRepository.getByTitle(title);

        notificationService.alertQuestionRaised(title, fatwa.getId());
    }

    public void raiseQuestion(String title, String question, User asker, long topicId) {
        Topic topic = topicRepository.getById(topicId);
        raiseQuestion(title, question, asker, topic);
    }

    public void assignToMufti(Fatwa fatwa, User mufti) {
        fatwa.setMufti(mufti);
        fatwaRepository.save(fatwa);
    }

    public void assignToMufti(long fatwaId, long muftiId) {
        Fatwa fatwa = fatwaRepository.getById(fatwaId);
        User mufti = userRepository.getById(muftiId);
        fatwa.setMufti(mufti);
        fatwaRepository.save(fatwa);
    }

    @Transactional
    public void answerFatwa(Fatwa fatwa, Fatwa newfatwa, User mufti) {
        mufti = userRepository.getUserByUsername(mufti.getUsername());
        fatwa.setAnswer(newfatwa.getAnswer());
        fatwa.setTopic(newfatwa.getTopic());
        fatwa.setAnswered(true);
        fatwa.setAnsweredOn(new Date());

        updateTopicCounters(fatwa.getTopic(),newfatwa.getTopic());

        userRepository.save(mufti);
        fatwaRepository.save(fatwa);
    }

    @Transactional
    public void updateTopicCounters(Topic oldTopic, Topic newTopic) {
        if(oldTopic.getId() != newTopic.getId()) {
            oldTopic.decrementCounterBy(1);
            newTopic.incrementCounterBy(1);

            Category oldCategory = oldTopic.getCategory();
            Category newCategory = newTopic.getCategory();

            topicRepository.save(oldTopic);
            topicRepository.save(newTopic);

            System.out.println(oldTopic.getName() + " -- " + newTopic.getName());

            if(oldCategory.getId() != newCategory.getId()) {
                oldCategory.decrementCounterBy(1);
                newCategory.incrementCounterBy(1);

                categoryRepository.save(oldCategory);
                categoryRepository.save(newCategory);
                System.out.println(oldCategory.getName() + " == " + newCategory.getName());

            }
        }
    }

    @Transactional
    public void answerFatwa(Fatwa fatwa, String answer, User mufti) {
        mufti = userRepository.getUserByUsername(mufti.getUsername());
        fatwa.setAnswer(answer);
        fatwa.setAnswered(true);
        fatwa.setAnsweredOn(new Date());
        userRepository.save(mufti);
        fatwaRepository.save(fatwa);
    }

    @Transactional
    public void saveAnswer(Fatwa fatwa, User mufti) {
        fatwa.setAnswered(true);
        fatwa.setAnsweredOn(new Date());
        userRepository.save(mufti);
        fatwaRepository.save(fatwa);
    }

    public boolean canDeselect(Fatwa fatwa) {

        return (fatwa.getMufti() != null && !fatwa.isAnswered());
    }

    public void approveAnswer(Fatwa fatwa, String fatwaId, User approvedBy) {
        fatwa.setApproved(true);
        fatwa.setApprovedBy(approvedBy);
        fatwa.setFatwaId(fatwaId);
        fatwaRepository.save(fatwa);
    }

    public String approve(long fatwaId) {
        if(hasRole("MAINMUFTI")) {
            Optional<Fatwa> optionalFatwa = fatwaRepository.findById(fatwaId);
            if(optionalFatwa.isPresent()) {
                Fatwa fatwa = optionalFatwa.get();
                if(fatwa.isAnswered() && !fatwa.isApproved()) {
                    approveAnswer(fatwa, "temp-fatwaId"+fatwaId, getCurrentUser());
                    notificationService.alertAnswerPublished(fatwa.getTitle(), fatwa.getAsker());
                    return "Approved Successfully!";
                }
                else if(fatwa.isApproved())
                    return "Approved Successfully!";
            }
        }
        return "Can't Approve!";
    }

    public List<Category> loadCategories() {
        return categoryRepository.findAll();
    }

    public void initCategory() {
        Category category1 = createCategory("Faiths & Beliefs");
        categoryRepository.save(category1);

        Arrays.asList("Islamic Beliefs",
                "World Religions",
                "False Sects",
                "Deviant Sects",
                "Innovations & Customs",
                "Taqleed & Fighi Schools",
                "The Holy Quran",
                "Hadith & Sunnah",
                "Dawah & Tableeg").forEach(topic -> createTopic(topic,category1));

        Category category2 = createCategory("Miscellaneous");
        categoryRepository.save(category2);

        Arrays.asList(
                "Halal & Haram",
                "Dua (Supplications)",
                "Islamic Names",
                "Tasawwuf",
                "History & Biography",
                "International Relations",
                "Others"
        ).forEach(topic -> createTopic(topic,category2));

        Category category3 = createCategory("Prayers & Duties");
        categoryRepository.save(category3);

        Arrays.asList(
                "Taharah (Purity)",
                "Salah (Prayer)",
                "Jumuah & Eid Prayers",
                "Death & Funeral",
                "Sawm (Fasting)",
                "Zakat & Charity",
                "Hajj & Umrah",
                "Oaths & Vows",
                "Waqf, Mosque, Madrasa",
                "Qurbani (Slaughtering)"
        ).forEach(topic -> createTopic(topic,category3));

        Category category4 = createCategory("Social Matters");
        categoryRepository.save(category4);

        Arrays.asList(
                "Nikah (Marriage)",
                "Talaq (Divorce)",
                "Food & Drinks",
                "Clothing & Lifestyle",
                "Rights & Etiquettes",
                "Education & Upbringing",
                "Women's Issues"
        ).forEach(topic -> createTopic(topic,category4));

        Category category5 = createCategory("Transactions & Dealings");
        categoryRepository.save(category5);

        Arrays.asList(
                "Business",
                "Shares & Investments",
                "Interest & Insurance",
                "Other Transactions",
                "Inheritance & Will",
                "Penal Code"
        ).forEach(topic -> createTopic(topic,category5));

    }

    public Category createCategory(String categoryName) {
        Category category = categoryRepository.getCategoryByName(categoryName);
        if(category == null) {
            category = new Category(categoryName);
            category.setCounter(0l);
            category.setDate(new Date());
            categoryRepository.save(category);
        }
        category = categoryRepository.getCategoryByName(categoryName);
        return category;
    }

    public void createTopic(String topicName, Category category) {
        Topic topic = new Topic(topicName);
        topicRepository.save(topic);
        System.out.println(category.getName() + ": " + topicName);
        topic = topicRepository.getTopicByName(topicName);
        topic.setCategory(category);
        topicRepository.save(topic);
    }

    public void createFatwa(User asker, User mufti,
                              Topic topic, String title, String question,
                              String answer, String fatwaId, Tag tag) {


        raiseQuestion(title, question, asker, topic);

        Fatwa fatwa = fatwaRepository.getByTitle(title);

        assignToMufti(fatwa, mufti);
        answerFatwa(fatwa, answer, mufti);

        if(tag != null)
            addTagToFatwa(fatwa, tag);

        fatwa = fatwaRepository.getByTitle(title);
        approveAnswer(fatwa,fatwaId, mufti);
    }

    private void addTagToFatwa(Fatwa fatwa, Tag tag) {
        fatwa.addTag(tag);
        fatwaRepository.save(fatwa);
        tag.incrementCounterBy(1);
        tagRepository.save(tag);
    }

    public void initTags() {
        Tag tag = new Tag("Special", 0l);
        tagRepository.save(tag);

        tag = new Tag("Modern", 0l);
        tagRepository.save(tag);
    }

    public Modal getModal(long fatwaId, long muftiId) {

        Optional<Fatwa> optionalFatwa = fatwaRepository.findById(fatwaId);
        if(optionalFatwa.isEmpty()) {
            return new Modal("Question Not Found - #" + fatwaId);
        }

        Optional<User> optionalMufti = userRepository.findById(muftiId);
        if(optionalMufti.isEmpty()) {
            return new Modal("User Not Found - #" + muftiId);
        }

        User mufti = optionalMufti.get();
        Modal modal = new Modal();
        modal.setId("fm"+fatwaId);

        boolean isMufti = hasRole(mufti, "MUFTI");
        if(!isMufti) {
            modal.setTitle("Some Error occurred");
            return modal;
        }

        Fatwa fatwa = optionalFatwa.get();
        modal.setFatwa(fatwa);

        boolean isMainMufti = hasRole(mufti, "MAINMUFTI");
        User assignedMufti = fatwa.getMufti();
        boolean isSameMufti = assignedMufti != null && assignedMufti.getUsername().equalsIgnoreCase(mufti.getUsername());

        if(fatwa.isApproved() && isMainMufti) {
            modal.addDisapproveButton(fatwaId);
        }
        else if(fatwa.isAnswered()) {
            if(isMainMufti){
                modal.addApproveButton(fatwaId);
                modal.addEditButton(fatwaId);
            } else {
                if(isSameMufti)
                    modal.addEditButton(fatwaId);
            }
        }
        else {
            if(isSameMufti) {
                modal.addDeselectButton(fatwaId);
            }
            modal.addAnswerButton(fatwaId);
        }
        if(isMainMufti){
            modal.addDeleteButton(fatwaId);
        }
        //modal.addCloseButton();
        return modal;
    }

    @PostConstruct
    public void initData() {
        /*initRoles();
        initUsers();
        fatwaService.initCategory();
        fatwaService.initTags();
        initFatawa();*/
    }


    private void initFatawa() {

        User asker = userRepository.getUserByUsername("user");
        User mufti = userRepository.getUserByUsername("mufti");

        Topic topic = topicRepository.getTopicByName("World Religions");
        Tag special = tagRepository.getTagByTagName("Special");
        Tag modern = tagRepository.getTagByTagName("Modern");

        String title = "Credit Card Issue";
        String question =
                "If you buy something by Credit Card in 12-36 months installments " +
                        "you have to pay 12% or 15% more than the purchase price, " +
                        "is it permissible to buy in this way?";

        String answer = "It is permissible to buy and sell by credit card on the condition that in case of purchase the amount is paid within the fixed period so that interest is not charged. In case of purchasing through credit card if you have to pay interest due to exceeding the fixed time, then this deal will become interest based and unlawful. So, if you buy something with a credit card for 12 or 36 months installments in which you have to pay 12 or 15 percent interest, then this deal will involve interest and become unlawful. You should never do this.\n";

        String fatwaId = "333/231/L=3/1443";

        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, null);

        topic = topicRepository.getTopicByName("Taharah (Purity)");
        title = "Can we pray with the clothes that we had sexual Intercourse?";
        question = "If we had sexual intercourse with an abaya and hijab can we pray with that clothes after? Should we wash them or we can’t wear them at all for offer salah?";
        answer = "If the clothes are clean and no impurity is smeared, then it is lawful to offer salah in those clothes.";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, special);

        topic = topicRepository.getTopicByName("Taharah (Purity)");
        title = "Is it necessary to do ghusl after smear test?";
        question = "Is it necessary to do ghusl after smear test?";
        answer = "Ghusl is not fardh on woman after having a smear test or any such other test; so there is no need to take a bath after undergoing such a test.";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, modern);

        topic = topicRepository.getTopicByName("Zakat & Charity");
        title = "Zakah on purchase price or sale price";
        question = "I have electronic items in my shop if I want to pay zakat of that items shall I pay zakat of purchase rate or sale rate? I have no fix rates; it depends on item and costumer condition.";
        answer = "You will pay zakah according to selling price.";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, null);

        topic = topicRepository.getTopicByName("Sawm (Fasting)");
        title = "Nafl fasting sehri";
        question = "For nafl fasting, if we take sehri and found that the time is over; should I continue the fasting.";
        answer = "If a person ate sehri to observe nafl fast and later found out that the time for sehri was expired, then it would not be valid for him to observe nafl fast. He can fast on another day by eating sehri on right time.";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, null);

        topic = topicRepository.getTopicByName("Salah (Prayer)");
        title = "Traveler’s Salah for University student";
        question = "I am a university student travelling, from my dorm where I live most of the year, to my home to see my parents for the weekend and the journey is over 100 miles. Am I still able to pray the 2 rakat traveler's salaah when I am at my parents’ home where I only now live during university holidays?";
        answer = "Your stay at the university or in the city where the university is located is for the purpose of education only and thus it is temporary. You do not have the intention of permanent residence there and probably the native place of your parents is your permanent place of residence; therefore, you will not do qasr in four rakah salah at your home if you come back from a distance of one hundred miles, rather you will perform full four rakah. However, if the distance between the two cities is 77.25 km or more, then you will be a musafir on the way and you will perform 2 rakah of four rakah salah on the way till you reach home.";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, special);

        topic = topicRepository.getTopicByName("Taharah (Purity)");
        title = "Do Madhi and Wadi Have Smell? And Can You Pray Salah in Same Cloth?";
        question = "Sometimes my pants are wet and I don't even know when it becomes. I think it is mazi or wadi. But I've read they have no smell. But little smell comes. And can I pray in it? If I can't pray in the same cloth then can I pray it in it if it's absolutely necessary?";
        answer = "There is no odour in Madhi or Wadi, but both are unclean. It is not lawful to offer salah without washing it if it is more than a dirham. Therefore, when your pants get wet with madhi, you should change it or wash it and offer salah.";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, special);

        topic = topicRepository.getTopicByName("Salah (Prayer)");
        title = "Congregational Prayer at home";
        question = "(1) I have a question regarding praying at home. Recently I have been placed under quarantine order at home due to one my family member had covid 19. So, is it okay to do congregational prayer at home with my mom and sister? Note that I'm the only male at home. And will I get reward of congregational Prayer? (2) Another question: Is there any difference in reward for congregational Prayer at mosque and outside mosque (at home/office)?";
        answer = "(1) Yes! You can offer salah with jamat at home with your mother and real sister. You become the Imam and stand in the front row and your mother and sister will stand behind you in a row. You will get reward of jamat while offering salah in this way.\n" +
                "\n" +
                "(2) You will get reward of jamat while offering salah with jamat outside the mosque at home or in the office. However, you will not get reward of performing Salah in mosque. And praying with jamat in the mosque is twenty-seven times more rewarding. ";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, modern);

        topic = topicRepository.getTopicByName("Inheritance & Will");
        title = "Distribution of money from sale of land";
        question = "If value of property is 1 crore, then what is the share of each of the following family member as per Islamic law: 1 mother 2 sons 4 daughters.";
        answer = "In the question mentioned above, if mother refers to the wife of the deceased who is the mother of the children and the deceased left only 2 sons and 4 daughters in addition to a wife among his heirs i.e. the deceased's mother, father, grandfather (Dada), grandmother (Dadi) and grandmother (Nani) have already died, then the entire property of the deceased will be divided into 64 shares after paying due rights preceding inheritance. Out of total, 8 shares will be given to the widow, 14-14 shares to each son and 7-7 shares to each daughter. And property worth one crore left by the deceased will be divided among his heirs in the same proportion. The detail of Shariah division is as follows:";
        createFatwa(asker, mufti, topic, title, question, answer, fatwaId, null);

    }

    private void initUsers() {
        Role userRole = roleRepository.getRoleByName("USER");
        Role muftiRole = roleRepository.getRoleByName("MUFTI");
        Role mainMuftiRole = roleRepository.getRoleByName("MAINMUFTI");


        //createDummyUser("user");

        //User savedUser = userRepository.getUserByUsername("user");
        //savedUser.addRole(userRole);
        //userRepository.save(savedUser);

        createDummyUser("mufti2");

        User savedUser = userRepository.getUserByUsername("mufti2");
        savedUser.addRole(userRole);
        savedUser.addRole(muftiRole);
        //savedUser.addRole(mainMuftiRole);
        savedUser.setAnswerCount(0l);
        userRepository.save(savedUser);
    }

    @Transactional
    public void createDummyUser(String userName) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode("123"));
        user.setAddress("Street");
        user.setCity("City");
        user.setCountry("Country");
        user.setEnabled(true);
        user.setState("myState");
        user.setFullName(userName);
        user.setEmail("test");
        user.setZipcode("12345");
        userRepository.save(user);
    }

    private void initRoles() {
        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        Role muftiRole = new Role();
        muftiRole.setName("MUFTI");
        roleRepository.save(muftiRole);

        Role mainMuftiRole = new Role();
        mainMuftiRole.setName("MAINMUFTI");
        roleRepository.save(mainMuftiRole);
    }
}
