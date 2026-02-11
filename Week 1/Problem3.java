import java.util.*;

class DNSCache {

    class Entry {
        String ip;
        long expiry;
        Entry(String ip, long ttl) {
            this.ip = ip;
            this.expiry = System.currentTimeMillis() + ttl * 1000;
        }
    }

    private int capacity;
    private LinkedHashMap<String, Entry> cache;
    private int hits = 0, misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, Entry> e) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    public String resolve(String domain) {
        long now = System.currentTimeMillis();
        if (cache.containsKey(domain)) {
            Entry e = cache.get(domain);
            if (e.expiry > now) {
                hits++;
                return e.ip;
            }
            cache.remove(domain);
        }
        misses++;
        String ip = queryUpstream(domain);
        cache.put(domain, new Entry(ip, 5));
        return ip;
    }

    private String queryUpstream(String domain) {
        return "172.217.14." + new Random().nextInt(255);
    }

    public String getStats() {
        int total = hits + misses;
        double rate = total == 0 ? 0 : (hits * 100.0 / total);
        return "Hit Rate: " + rate + "%";
    }

    public static void main(String[] args) {
        DNSCache d = new DNSCache(3);
        System.out.println(d.resolve("google.com"));
        System.out.println(d.resolve("google.com"));
        System.out.println(d.getStats());
    }
}
