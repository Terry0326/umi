CREATE TABLE blacklist (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  reason        VARCHAR(255),
  user_id       BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE block_users (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  action        INTEGER,
  blocker_id    BIGINT,
  executor_id   BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE cities (
  id           BIGINT NOT NULL AUTO_INCREMENT,
  city_code    VARCHAR(255),
  city_name    VARCHAR(255),
  country_code VARCHAR(255),
  country_name VARCHAR(255),
  state_code   VARCHAR(255),
  state_name   VARCHAR(255),
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE client_version (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  app_id        VARCHAR(50),
  description   VARCHAR(255),
  download_url  VARCHAR(100),
  platform      INTEGER,
  version_name  VARCHAR(50),
  version_num   INTEGER,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE countries (
  id                BIGINT NOT NULL,
  chinese_name      VARCHAR(255),
  dialing_code      VARCHAR(255),
  english_full_name VARCHAR(255),
  english_name      VARCHAR(255),
  memo              VARCHAR(255),
  num_code          VARCHAR(255),
  three_letter_code VARCHAR(255),
  two_letter_code   VARCHAR(255),
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE general_codes (
  id        BIGINT NOT NULL AUTO_INCREMENT,
  category  INTEGER,
  code      VARCHAR(255),
  deleted   BIT    NOT NULL,
  name      VARCHAR(50),
  order_num INTEGER,
  parent_id BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE institutions (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE messages (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  content       VARCHAR(1000),
  countries     VARCHAR(255),
  expired       BIT    NOT NULL,
  link_id       BIGINT,
  link_type     INTEGER,
  notify_time   DATETIME,
  is_read       BIT,
  read_time     DATETIME,
  title         VARCHAR(200),
  message_type  INTEGER,
  from_user_id  BIGINT,
  to_user_id    BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE nation (
  id       BIGINT NOT NULL AUTO_INCREMENT,
  city     VARCHAR(100),
  code     VARCHAR(10),
  district VARCHAR(100),
  province VARCHAR(100),
  parent   BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE resources (
  id             BIGINT NOT NULL AUTO_INCREMENT,
  creation_time  DATETIME,
  deleted        BIT,
  update_time    DATETIME,
  completed      BIT    NOT NULL,
  description    VARCHAR(500),
  download_url   VARCHAR(200),
  mime_type      VARCHAR(50),
  name           VARCHAR(100),
  path           VARCHAR(100),
  play_url       VARCHAR(200),
  possessor_id   BIGINT,
  possessor_type INTEGER,
  size           INTEGER,
  snapshot       BIT    NOT NULL,
  type           INTEGER,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE role (
  id          BIGINT NOT NULL AUTO_INCREMENT,
  description VARCHAR(64),
  name        VARCHAR(20),
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE sensitive_word (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  name          VARCHAR(255),
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE system_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
)
  ENGINE = InnoDB;
CREATE TABLE system_users (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  account_expired     BIT          NOT NULL,
  account_locked      BIT          NOT NULL,
  creation_time       DATETIME,
  credentials_expired BIT          NOT NULL,
  description         VARCHAR(500),
  account_enabled     BIT,
  password            VARCHAR(255) NOT NULL,
  real_name           VARCHAR(50),
  role_name           VARCHAR(255),
  telephone           VARCHAR(20),
  update_time         DATETIME,
  username            VARCHAR(50)  NOT NULL,
  creator_id          BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE third_party_accounts (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  acc_id        VARCHAR(50),
  acc_name      VARCHAR(50),
  extra         VARCHAR(200),
  source        INTEGER,
  third_party   INTEGER,
  token         VARCHAR(200),
  user_id       BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE topic_comments (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  is_burnable   BIT,
  burned        BIT    NOT NULL,
  content       VARCHAR(500),
  publisher_id  BIGINT,
  topic_id      BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE topic_like (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  topic_id      BIGINT,
  user_id       BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE topic_tip_off (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  done          BIT    NOT NULL,
  done_str      VARCHAR(255),
  reason        VARCHAR(500),
  topic_id      BIGINT,
  user_id       BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE topics (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  address       VARCHAR(200),
  building_name VARCHAR(200),
  lat           DOUBLE PRECISION,
  lng           DOUBLE PRECISION,
  alive_time    INTEGER,
  anonymous     BIT    NOT NULL,
  burn_topic    BIT    NOT NULL,
  burned        BIT    NOT NULL,
  click_num     BIGINT,
  comments_num  BIGINT,
  content       VARCHAR(100),
  cover         VARCHAR(100),
  description   VARCHAR(1000),
  enabled       BIT    NOT NULL,
  like_num      BIGINT,
  topic_type    INTEGER,
  weight        INTEGER,
  user_id       BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE user_follows (
  id             BIGINT NOT NULL AUTO_INCREMENT,
  creation_time  DATETIME,
  deleted        BIT,
  update_time    DATETIME,
  follower_id    BIGINT,
  target_user_id BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
)
  ENGINE = InnoDB;
CREATE TABLE user_tip_off (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  creation_time DATETIME,
  deleted       BIT,
  update_time   DATETIME,
  done          BIT    NOT NULL,
  done_str      VARCHAR(255),
  reason        VARCHAR(500),
  tip_user_id   BIGINT,
  user_id       BIGINT,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
CREATE TABLE users (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  creation_time       DATETIME,
  deleted             BIT,
  update_time         DATETIME,
  account_expired     BIT          NOT NULL,
  account_locked      BIT          NOT NULL,
  address             VARCHAR(150),
  city                VARCHAR(100),
  city_code           VARCHAR(10),
  country             VARCHAR(100),
  country_code        VARCHAR(100),
  district            VARCHAR(50),
  district_code       VARCHAR(10),
  lat                 DOUBLE PRECISION,
  lng                 DOUBLE PRECISION,
  province            VARCHAR(100),
  province_code       VARCHAR(10),
  avatar              VARCHAR(50),
  comment_num         BIGINT,
  credentials_expired BIT          NOT NULL,
  dialing_code        VARCHAR(10),
  account_enabled     BIT,
  followers_num       BIGINT,
  following_num       BIGINT,
  gender              INTEGER,
  info_completed      BIT          NOT NULL,
  last_login_time     DATETIME,
  nickname            VARCHAR(100),
  password            VARCHAR(255) NOT NULL,
  publish_topics_num  BIGINT,
  registration_way    INTEGER,
  signature           VARCHAR(200),
  status              INTEGER,
  topic_like_num      BIGINT,
  username            VARCHAR(50)  NOT NULL,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB;
ALTER TABLE system_users ADD CONSTRAINT UK_tr0kj1o2dqfwm13a6fvwrg867 UNIQUE (username);
ALTER TABLE users ADD CONSTRAINT UK_2ty1xmrrgtn89xt7kyxx6ta7h UNIQUE (nickname);
ALTER TABLE users ADD CONSTRAINT UK_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);
ALTER TABLE blacklist ADD CONSTRAINT FK8rrfcw4ypm4eayqtfu53tkl9r FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE block_users ADD CONSTRAINT FKrab54pef2lbi10s4sr51ekyhd FOREIGN KEY (blocker_id) REFERENCES users (id);
ALTER TABLE block_users ADD CONSTRAINT FK4jw2tgn9m35jbdf6vv059cpkg FOREIGN KEY (executor_id) REFERENCES users (id);
ALTER TABLE general_codes ADD CONSTRAINT FKdqvy3n3u0yb550hpxxo3tdipa FOREIGN KEY (parent_id) REFERENCES general_codes (id);
ALTER TABLE messages ADD CONSTRAINT FKms9o5dx3lfmikr6k8kyxi030e FOREIGN KEY (from_user_id) REFERENCES users (id);
ALTER TABLE messages ADD CONSTRAINT FK6y0sbofpv484p7yi78nr9hii1 FOREIGN KEY (to_user_id) REFERENCES users (id);
ALTER TABLE nation ADD CONSTRAINT FKebfjmxf7bo2spp9kyvu9wtnw2 FOREIGN KEY (parent) REFERENCES nation (id);
ALTER TABLE system_user_role ADD CONSTRAINT FKl71b9ib2ylhgyby4x7r5bof17 FOREIGN KEY (role_id) REFERENCES role (id);
ALTER TABLE system_user_role ADD CONSTRAINT FKrsidvnc418ibn3m8ij1xkjrau FOREIGN KEY (user_id) REFERENCES system_users (id);
ALTER TABLE system_users ADD CONSTRAINT FKth5xkj150a0p3eg40rqlg42u5 FOREIGN KEY (creator_id) REFERENCES system_users (id);
ALTER TABLE third_party_accounts ADD CONSTRAINT FK3c2omice7eobotlf11ksn6ohh FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE topic_comments ADD CONSTRAINT FKjh3wv8ri2mjm8purn0vmgp6nb FOREIGN KEY (publisher_id) REFERENCES users (id);
ALTER TABLE topic_comments ADD CONSTRAINT FKrxqp6e3ekumm5lpd0et115732 FOREIGN KEY (topic_id) REFERENCES topics (id);
ALTER TABLE topic_like ADD CONSTRAINT FK6y9ncydlmgu4ujxdk6rgtcr1i FOREIGN KEY (topic_id) REFERENCES topics (id);
ALTER TABLE topic_like ADD CONSTRAINT FKp0k3ohv3nnmlsi4mtn3rs85br FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE topic_tip_off ADD CONSTRAINT FK93cylckgideapeevoj871syp0 FOREIGN KEY (topic_id) REFERENCES topics (id);
ALTER TABLE topic_tip_off ADD CONSTRAINT FKddvkpiqcms86w1rtjocw63f8w FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE topics ADD CONSTRAINT FKoc3papwmjontq89fcia02ag1h FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE user_follows ADD CONSTRAINT FKqx9mu1fniaua5jfe1cdyspxdt FOREIGN KEY (follower_id) REFERENCES users (id);
ALTER TABLE user_follows ADD CONSTRAINT FKjode808o7u2b6xo2oovjgq55p FOREIGN KEY (target_user_id) REFERENCES users (id);
ALTER TABLE user_role ADD CONSTRAINT FKa68196081fvovjhkek5m97n3y FOREIGN KEY (role_id) REFERENCES role (id);
ALTER TABLE user_role ADD CONSTRAINT FKj345gk1bovqvfame88rcx7yyx FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE user_tip_off ADD CONSTRAINT FKm9lct8j5prnt49y2igxueotqc FOREIGN KEY (tip_user_id) REFERENCES users (id);
ALTER TABLE user_tip_off ADD CONSTRAINT FK77naxet81u69m28ljq7x7vxp5 FOREIGN KEY (user_id) REFERENCES users (id);