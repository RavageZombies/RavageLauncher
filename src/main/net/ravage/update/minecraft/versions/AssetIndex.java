package net.ravage.update.minecraft.versions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssetIndex
{
    public static String DEFAULT_ASSET_NAME = "legacy";
    private Map<String, AssetObject> objects;
    private boolean virtual;
    
    public AssetIndex() {
        this.objects = new LinkedHashMap<String, AssetObject>();
    }
    
    public Map<String, AssetObject> getFileMap() {
        return this.objects;
    }
    
    public Map<AssetObject, String> getUniqueObjects() {
        Map<AssetObject, String> result = new HashMap<AssetObject, String>();
        for (Map.Entry<String, AssetObject> objectEntry : this.objects.entrySet()) {
            result.put(objectEntry.getValue(), objectEntry.getKey());
        }
        return result;
    }
    
    public boolean isVirtual() {
        return this.virtual;
    }
    
    public class AssetObject
    {
        private String hash;
        private long size;
        private boolean reconstruct;
        private String compressedHash;
        private long compressedSize;
        
        public String getHash() {
            return this.hash;
        }
        
        public long getSize() {
            return this.size;
        }
        
        public boolean shouldReconstruct() {
            return this.reconstruct;
        }
        
        public boolean hasCompressedAlternative() {
            return this.compressedHash != null;
        }
        
        public String getCompressedHash() {
            return this.compressedHash;
        }
        
        public long getCompressedSize() {
            return this.compressedSize;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AssetObject that = (AssetObject)o;
            if (this.compressedSize != that.compressedSize) {
                return false;
            }
            if (this.reconstruct != that.reconstruct) {
                return false;
            }
            if (this.size != that.size) {
                return false;
            }
            Label_0106: {
                if (this.compressedHash != null) {
                    if (this.compressedHash.equals(that.compressedHash)) {
                        break Label_0106;
                    }
                }
                else if (that.compressedHash == null) {
                    break Label_0106;
                }
                return false;
            }
            if (this.hash != null) {
                if (this.hash.equals(that.hash)) {
                    return true;
                }
            }
            else if (that.hash == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = (this.hash != null) ? this.hash.hashCode() : 0;
            result = 31 * result + (int)(this.size ^ this.size >>> 32);
            result = 31 * result + (this.reconstruct ? 1 : 0);
            result = 31 * result + ((this.compressedHash != null) ? this.compressedHash.hashCode() : 0);
            result = 31 * result + (int)(this.compressedSize ^ this.compressedSize >>> 32);
            return result;
        }
    }
}
